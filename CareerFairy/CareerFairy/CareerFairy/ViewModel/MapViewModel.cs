using CareerFairy.Model;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Text.RegularExpressions;
using System.Windows.Controls;
using System.IO;

namespace CareerFairy.ViewModel
{
    /// <summary>
    /// View model for the booth map.
    /// Contains most of the logic for interacting with the boothmap
    /// </summary>
    class MapViewModel : ObservableObject
    {
        // mapImage is a composite drawing of the overlay and the baseMap
        private DrawingImage mapImage;
        // Overlay is the drawinggroup containing the booths
        private DrawingGroup overlay;
        // Lowest level containing the map of the venue
        private ImageDrawing baseMap;
        // Underlying boothmap object
        private BoothMap boothMap;
        // Set of all booths currently drawn, used for hittesting
        private HashSet<BoothWrapper> drawnBooths;
        // The current setting that tells us what to do when the user clicks
        private ClickSetting clickSetting;
        // Holds the reference to the booth being dragged when applicable
        private BoothWrapper draggedBooth;
        // Used to make drawing straight columns or rows of booths easier
        private DragDirection dragDirection;
        // Character used for creating the names of the booths
        private char rowChar = 'A';
        // THis enum contains the possible settings the mouse might be in based on the buttons in the view
        public enum ClickSetting { Add, Edit, Delete }
        // Drag direction locks in the drag for adding booths to either Left/Right or Up/Down
        private enum DragDirection { None, LR, UD }

        public ClickSetting CurrentSetting
        {
            get
            {
                return clickSetting;
            }
            set
            {
                clickSetting = value;
                RaisePropertyChangedEvent("CurrentSetting");
            }
        }

        public MapViewModel()
        {
            boothMap = new BoothMap();
            overlay = new DrawingGroup();
            baseMap = new ImageDrawing();
            DrawingGroup mapDrawing = new DrawingGroup();
            mapDrawing.Children.Add(baseMap);
            mapDrawing.Children.Add(overlay);
            drawnBooths = new HashSet<BoothWrapper>();
            this.mapImage = new DrawingImage(mapDrawing);
            this.populateFromModel();
            this.clickSetting = ClickSetting.Add;
        }


        /// <summary>
        /// Saves the current mapImage to a .png file
        /// </summary>
        /// <param name="fileName">The location to save the .png file</param>
        public void SaveImage(string fileName)
        {
            /*var drawingImage = new Image { Source = mapImage };
            var bitmap = new RenderTargetBitmap((int) baseMap.Bounds.Width, (int)baseMap.Bounds.Height, 96, 96, PixelFormats.Pbgra32);
            bitmap.Render(drawingImage);
            var encoder = new PngBitmapEncoder();
            encoder.Frames.Add(BitmapFrame.Create(bitmap));

            using (var stream = new FileStream(fileName, FileMode.Create))
            {
                encoder.Save(stream);
            }*/
            Rect drawingBounds = baseMap.Bounds;
            int pixelWidth = (int)drawingBounds.Width;
            int pixelHeight = (int)drawingBounds.Height;
            double dpiX = 96;
            double dpiY = 96;
            DrawingVisual drawingVisual = new DrawingVisual();
            DrawingContext drawingContext = drawingVisual.RenderOpen();

            drawingContext.DrawDrawing(mapImage.Drawing);
            drawingContext.Close();

            RenderTargetBitmap targetBitmap = new RenderTargetBitmap(pixelWidth, pixelHeight, dpiX, dpiY, PixelFormats.Pbgra32);

            targetBitmap.Render(drawingVisual);
            var encoder = new PngBitmapEncoder();
            encoder.Frames.Add(BitmapFrame.Create(targetBitmap));
            using (var stream = new FileStream(fileName, FileMode.Create))
            {
                encoder.Save(stream);
            }
        }

        public MapViewModel(BoothMap bm)
        {
            boothMap = bm;
            drawnBooths = new HashSet<BoothWrapper>();
            overlay = new DrawingGroup();
            baseMap = new ImageDrawing();
            DrawingGroup mapDrawing = new DrawingGroup();
            mapDrawing.Children.Add(baseMap);
            mapDrawing.Children.Add(overlay);
            this.clickSetting = ClickSetting.Add;
            this.mapImage = new DrawingImage(mapDrawing);
            this.populateFromModel();
        }

        /// <summary>
        /// Handles adding a single booth, changing the name of a booth, or deleting a single booth on a mouse click
        /// </summary>
        /// <param name="point">The location the mouse is pressed</param>
        public void clickHandler(Point point)
        {
            BoothWrapper hitTestedBooth = hitTestPoint(point);
            if (clickSetting == ClickSetting.Add)
            {
                Booth addedBooth = new Booth(rowChar + "1", point);
                incrementRowChar();
                drawBooth(addedBooth);
                this.BoothMapModel.BoothList.Add(addedBooth);
            } else if (clickSetting == ClickSetting.Delete && hitTestedBooth != null) {
                this.overlay.Children.Remove(hitTestedBooth.DrawingGroup);
                this.drawnBooths.Remove(hitTestedBooth);
                hitTestedBooth.DragSet.Remove(hitTestedBooth);
                this.BoothMapModel.BoothList.Remove(hitTestedBooth.Booth);
                if (hitTestedBooth.Booth.Assignment != null)
                    hitTestedBooth.Booth.Assignment.Assignment = null;
            } else if (clickSetting == ClickSetting.Edit && hitTestedBooth != null) {
                EditForm editForm = new EditForm();
                editForm.DataContext = hitTestedBooth.Booth;
                editForm.ShowDialog();
                overlay.Children.Remove(hitTestedBooth.DrawingGroup);
                hitTestedBooth.DrawingGroup = justDraw(hitTestedBooth.Booth);
                overlay.Children.Add(hitTestedBooth.DrawingGroup);
            }
        }

        private void incrementRowChar()
        {
            rowChar++;
            if (rowChar > 'Z')
            {
                rowChar = 'A';
            }
        }

        /// <summary>
        /// Called when the user starts to drag the mouse
        /// </summary>
        /// <param name="point">Starting point of the drag</param>
        public void dragStart(Point point)
        {
            draggedBooth = hitTestPoint(point);
            dragDirection = DragDirection.None;
        }

        /// <summary>
        /// Called when the user releases their mouse from a drag
        /// </summary>
        public void dragStop()
        {
            draggedBooth = null;
            dragDirection = DragDirection.None;
        }

        /// <summary>
        /// Called while the user is dragging.
        /// Handles adding rows or columns of booths,
        /// Removing multiple booths,
        /// repositioning a group of booths
        /// </summary>
        /// <param name="lastpoint">The point where the mouse was last</param>
        /// <param name="newPoint">The new position of the mouse</param>
        public void dragHandler(Point lastpoint, Point newPoint)
        {
            if (clickSetting == ClickSetting.Add)
            {
                // If we are dragging left and right only consider the change in x position,
                // If up and down only consider y delta
                if (dragDirection == DragDirection.LR)
                {
                    newPoint = new Point(newPoint.X, draggedBooth.Booth.Position.Y);
                } else if (dragDirection == DragDirection.UD)
                {
                    newPoint = new Point(draggedBooth.Booth.Position.X, newPoint.Y);
                }
                BoothWrapper lastBooth = draggedBooth;
                BoothWrapper newBooth = hitTestPoint(newPoint);
                if (newBooth != null)
                {
                    draggedBooth = newBooth;
                }
                if (newBooth == null && lastBooth == null)
                {
                    Booth addedBooth = new Booth(rowChar + "1", newPoint);
                    draggedBooth = drawBooth(addedBooth);
                    incrementRowChar();
                    this.BoothMapModel.BoothList.Add(addedBooth);
                }
                else if (newBooth == null)
                {
                    Vector mouseMovement = new Vector(newPoint.X - lastBooth.Booth.Position.X, newPoint.Y - lastBooth.Booth.Position.Y);
                    Point boothPoint = new Point(lastBooth.Booth.Position.X, lastBooth.Booth.Position.Y);
                    bool createBooth = false;
                    // Determine where to place the booth and what to set the drag direction to
                    if (mouseMovement.X >= 15 && (dragDirection == DragDirection.None || dragDirection == DragDirection.LR))
                    {
                        boothPoint.X += 30;
                        dragDirection = DragDirection.LR;
                        createBooth = true;
                    }
                    else if (mouseMovement.X <= -15 && (dragDirection == DragDirection.None || dragDirection == DragDirection.LR))
                    {
                        boothPoint.X -= 30;
                        dragDirection = DragDirection.LR;
                        createBooth = true;
                    }
                    else if (mouseMovement.Y >= 10 && (dragDirection == DragDirection.None || dragDirection == DragDirection.UD))
                    {
                        boothPoint.Y += 20;
                        dragDirection = DragDirection.UD;
                        createBooth = true;
                    }
                    else if (mouseMovement.Y <= -10 && (dragDirection == DragDirection.None || dragDirection == DragDirection.UD))
                    {
                        boothPoint.Y -= 20;
                        dragDirection = DragDirection.UD;
                        createBooth = true;
                    }
                    // Create a new booth
                    if (createBooth)
                    {
                        string lastName = lastBooth.Booth.Name;
                        string namePat = @"^(.*?)(\d*)$";
                        Regex r = new Regex(namePat, RegexOptions.IgnoreCase);
                        Match m = r.Match(lastName);
                        int? num = null;
                        if (m.Groups[2].Value != "")
                        {
                            num = Int32.Parse(m.Groups[2].Value) + 1;
                        }
                        string newName = m.Groups[1].Value + (num?.ToString() ?? "");
                        Booth addedBooth = new Booth(newName, boothPoint);
                        draggedBooth = drawBooth(addedBooth, draggedBooth.DragSet);
                        this.BoothMapModel.BoothList.Add(addedBooth);
                        lastBooth = draggedBooth;
                    }
                }
            }
            else if (clickSetting == ClickSetting.Edit && draggedBooth != null)
            {
                // Change each booth in a booths dragset position based on the mousemovement vector
                Vector mouseMovement = new Vector(newPoint.X - draggedBooth.Booth.Position.X, newPoint.Y - draggedBooth.Booth.Position.Y);
                foreach (BoothWrapper bw in draggedBooth.DragSet)
                {
                    bw.Booth.Position = Point.Add(bw.Booth.Position, mouseMovement);
                    this.overlay.Children.Remove(bw.DrawingGroup);
                    bw.DrawingGroup = justDraw(bw.Booth);
                    overlay.Children.Add(bw.DrawingGroup);
                }
            }
            else if (clickSetting == ClickSetting.Delete)
            {
                // Removes any booth that is hittested under the current mouse's position
                BoothWrapper toDelete = hitTestPoint(newPoint);
                if (toDelete != null)
                {
                    this.overlay.Children.Remove(toDelete.DrawingGroup);
                    toDelete.DragSet.Remove(toDelete);
                    this.drawnBooths.Remove(toDelete);
                    this.BoothMapModel.BoothList.Remove(toDelete.Booth);
                    if (toDelete.Booth.Assignment != null)
                        toDelete.Booth.Assignment.Assignment = null;
                }
            }
        }

        // Generates the drwaing tree structure from the model
        private void populateFromModel()
        {
            baseMap.ImageSource = new BitmapImage(boothMap.BaseImagePath);
            //Change this in the future to allow panning and zooming
            this.Width = 1000;
            this.Height = 800;
            baseMap.Rect = new Rect(0, 0, Width, Height);
            overlay.Children.Clear();
            foreach (Booth booth in boothMap.BoothList)
            {
                drawBooth(booth);
            }
        }

        // Creates a drawing group for a given booth but it is not added to the overlay
        private DrawingGroup justDraw(Booth booth)
        {
            DrawingGroup boothDrawing = new DrawingGroup();
            // Draw rectangle
            GeometryDrawing borderRect = new GeometryDrawing();
            borderRect.Brush = Brushes.Transparent;
            Point topLeft = new Point(booth.Position.X - 15, booth.Position.Y - 10);
            Rect border = new Rect(topLeft, new Size(30, 20));
            borderRect.Geometry = new RectangleGeometry(border);
            borderRect.Pen = new Pen(Brushes.Black, 1);
            boothDrawing.Children.Add(borderRect);

            // Draw formatted text of the name
            FormattedText boothName = new FormattedText(booth.Name, CultureInfo.CurrentCulture, FlowDirection.LeftToRight,
                new Typeface("Arial"), 14, Brushes.Black);
            GeometryDrawing textDrawing = new GeometryDrawing();
            textDrawing.Brush = Brushes.Black;
            double nameWidth = boothName.Width;
            double nameHeight = boothName.Height;
            Point textPoint = new Point((booth.Position.X), (booth.Position.Y));
            textPoint.X -= nameWidth / 2;
            textPoint.Y -= nameHeight / 2;
            textDrawing.Geometry = boothName.BuildGeometry(textPoint);
            boothDrawing.Children.Add(textDrawing);
            return boothDrawing;
        }

        // Creates a boothwrapper class for a booth and adds it to a dragset and adds it to the overlay
        private BoothWrapper drawBooth(Booth booth, HashSet<BoothWrapper> dragSet)
        {
            DrawingGroup boothDrawing = justDraw(booth);
            overlay.Children.Add(boothDrawing);
            BoothWrapper bw = new BoothWrapper();
            bw.DrawingGroup = boothDrawing;
            bw.Booth = booth;
            drawnBooths.Add(bw);
            bw.DragSet = dragSet;
            dragSet.Add(bw);
            return bw;
        }

        // Creates a booth with an empty dragset
        private BoothWrapper drawBooth(Booth booth)
        {
            return drawBooth(booth, new HashSet<BoothWrapper>());
        }

        // Hittests a point against the drawn booths
        private BoothWrapper hitTestPoint(Point location)
        {
            foreach (BoothWrapper bw in drawnBooths)
            {
                if (((GeometryDrawing)bw.DrawingGroup.Children[0]).Geometry.FillContains(location))
                {
                    return bw;
                }
            }
            return null;
        }

        /// <summary>
        /// The boothwrapper class is a wrapper around a booth containing the auxiliary information used in drawing the booths
        /// </summary>
        private class BoothWrapper
        {
            public Booth Booth { get; set; }
            public DrawingGroup DrawingGroup { get; set; }
            public HashSet<BoothWrapper> DragSet { get; set; }
        }

        // Image source that is used in the view to display the actual map
        public ImageSource MapImageSource
        {
            get
            {
                return mapImage;
            }
        }

        // Gets and sets the underlying model
        public BoothMap BoothMapModel
        {
            get
            {
                return boothMap;
            }
            set
            {
                boothMap = value;
                this.populateFromModel();
            }
        }

        public double Width
        {
            get;
            set;
        }

        public double Height
        {
            get;
            set;
        }
    }
}
