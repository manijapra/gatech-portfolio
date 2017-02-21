using CareerFairy.Model;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
using Microsoft.Win32;
using System.Runtime.Serialization;

namespace CareerFairy.ViewModel
{
    /// <summary>
    /// Main Window viewmodel is the parent for all other viewmodels inside of the 
    /// application.
    /// This also contains the logic for loading and saving .cfair files.
    /// Responsible for handling most of the commands from the view.
    /// </summary>
    class MainWindowViewModel : ObservableObject
    {
        private int _selectedTab;

        public CareerFair CareerFair {get; set;}
        public ObservableCollection<TabItem> DayTabs { get; internal set; }

        public ICommand SaveCommand
        {
            get { return new DelegateCommand(Save); }
        }

        public ICommand ImportDataCommand
        {
            get { return new DelegateCommand(ImportData); }
        }

        public ICommand ExportMapCommand
        {
            get { return new DelegateCommand(ExportMap); }
        }

        public ICommand RandomizePlacementsCommand
        {
            get { return new DelegateCommand(RandomizePlacements); }
        }

        private void RandomizePlacements()
        {
            SelectedModel.RandomizePlacements();
            RaisePropertyChangedEvent("SelectedModel");
        }

        /// <summary>
        /// Saves the current map to a user selected location
        /// </summary>
        private void ExportMap()
        {
            SaveFileDialog saveDialog = new SaveFileDialog();
            saveDialog.Filter = "png files (*.png)|*.png";
            saveDialog.RestoreDirectory = true;
            Nullable<bool> result = saveDialog.ShowDialog();
            if (result == true)
            {
                this.SelectedModel.MapViewModel.SaveImage(saveDialog.FileName);
            }
        }

        /// <summary>
        /// Starts off the Dataload porcess
        /// </summary>
        private void ImportData()
        {
            OpenFileDialog openDialog = new OpenFileDialog();
            openDialog.Filter = "xls files (*.xls)|*.xls";
            openDialog.RestoreDirectory = true;
            Nullable<bool> result = openDialog.ShowDialog();
            if (result == true)
            {
                DataLoad dl = new DataLoad();
                dl.importData(openDialog.FileName);
                this.CareerFair = dl.createFair();
                createTabs();
                RaisePropertyChangedEvent("CareerFair");
                RaisePropertyChangedEvent("SelectedModel");
            }
        }

        /// <summary>
        /// Serializes and saves the underlying model to a user prompted location
        /// </summary>
        private void Save()
        {
            SaveFileDialog saveDialog = new SaveFileDialog();
            saveDialog.Filter = "cfair files (*.cfair)|*.cfair";
            saveDialog.RestoreDirectory = true;
            Nullable<bool> result = saveDialog.ShowDialog();
            if (result == true)
            {
                // Save document
                string filename = saveDialog.FileName;
                System.IO.FileStream fs = new FileStream(filename, FileMode.Create);
                DataContractSerializer dcs = new DataContractSerializer(typeof(CareerFair));
                dcs.WriteObject(fs, CareerFair);
                fs.Close();
            }
        }

        public ICommand LoadCommand
        {
            get { return new DelegateCommand(Load); }
        }

        /// <summary>
        /// Loads and deserializes a saved CareerFair object which it uses to populate the model
        /// </summary>
        private void Load()
        {
            OpenFileDialog openDialog = new OpenFileDialog();
            openDialog.Filter = "cfair files (*.cfair)|*.cfair";
            openDialog.RestoreDirectory = true;
            Nullable<bool> result = openDialog.ShowDialog();
            if (result == true)
            {
                // Load document
                string filename = openDialog.FileName;
                System.IO.FileStream fs = new FileStream(filename, FileMode.Open);
                DataContractSerializer dcs = new DataContractSerializer(typeof(CareerFair));
                this.CareerFair = (CareerFair)dcs.ReadObject(fs);
                fs.Close();
                RaisePropertyChangedEvent("CareerFair");
                DayTabs.Clear();
                foreach (EventDay day in this.CareerFair.Days)
                {
                    DayTabs.Add(new TabItem { Header = day.Name, Content = new EventDayViewModel(day) });
                }
                RaisePropertyChangedEvent("DayTabs");
            }
        }

        public MainWindowViewModel()
        {
            CareerFair = new CareerFair();
            DayTabs = new ObservableCollection<TabItem>();
            createTabs();
        }

        /// <summary>
        /// Creates the wrapper classes for the tabs and adds them to the observed collection.
        /// </summary>
        private void createTabs()
        {
            DayTabs.Clear();
            foreach (EventDay day in this.CareerFair.Days)
            {
                DayTabs.Add(new TabItem { Header = day.Name, Content = new EventDayViewModel(day) });
            }
        }

        /// <summary>
        /// Index of the selected tab in the view
        /// </summary>
        public int SelectedTab
        {
            get
            {
                return _selectedTab;
            }
            set
            {
                _selectedTab = value;
                RaisePropertyChangedEvent("SelectedModel");
            }
        }

        /// <summary>
        /// Returns the user selected viewmodel from the tabs
        /// </summary>
        public EventDayViewModel SelectedModel
        {
            get
            {
                if (DayTabs.Count == 0)
                    return null;
                return DayTabs[SelectedTab].Content;
            }
        }
    }

    /// <summary>
    /// This class is a wrapper around the EventDayViewModel to contain all of the information needed
    /// to display a tab in the main view.
    /// </summary>
    sealed class TabItem
    {
        public String Header { get; set; }
        public EventDayViewModel Content { get; set; }
    }

}
