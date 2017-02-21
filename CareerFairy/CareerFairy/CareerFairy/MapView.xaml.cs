using CareerFairy.ViewModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace CareerFairy
{
    /// <summary>
    /// Interaction logic for MapView.xaml
    /// </summary>
    public partial class MapView : UserControl
    {
        public MapView()
        {
            InitializeComponent();
            isDragging = false;
            isClicking = false;
        }

        private bool isDragging;
        private bool isClicking;
        private Point lastPoint;
        private Point startPoint;

        // Get rid of this and make it true MVVM
        private void Image_MouseUp(object sender, MouseButtonEventArgs e)
        {
            if (isClicking)
            {
                ((MapViewModel)(this.DataContext)).clickHandler(e.GetPosition(this));
            }
            if (isDragging)
            {
                ((MapViewModel)(this.DataContext)).dragStop();
            }
            isClicking = false;
            isDragging = false;
        }

        private void Image_MouseDown(object sender, MouseButtonEventArgs e)
        {
            isClicking = true;
            isDragging = false;
            startPoint = e.GetPosition(this);
            lastPoint = startPoint;
        }

        private void Image_MouseMove(object sender, MouseEventArgs e)
        {
            Point newPoint = e.GetPosition(this);
            if (isClicking && (Math.Abs(newPoint.X - startPoint.X) > 10 || Math.Abs(newPoint.Y - startPoint.Y) > 10))
            {
                isClicking = false;
                ((MapViewModel)(this.DataContext)).dragStart(startPoint);
                isDragging = true;
            }
            if (isDragging)
            {
                ((MapViewModel)(this.DataContext)).dragHandler(lastPoint, e.GetPosition(this));
            }
            lastPoint = newPoint;
        }

        private void Image_MouseEnter(object sender, MouseEventArgs e)
        {
            isDragging = false;
            isClicking = false;
        }

        private void Image_MouseLeave(object sender, MouseEventArgs e)
        {
            if (isDragging)
            {
                ((MapViewModel)(this.DataContext)).dragStop();
            }
            isDragging = false;
            isClicking = false;
        }
    }
}
