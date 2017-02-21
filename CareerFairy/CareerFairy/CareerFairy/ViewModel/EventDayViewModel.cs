using CareerFairy.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Globalization;

namespace CareerFairy.ViewModel
{
    /// <summary>
    /// Viewmodel for an EventDayView tab, contains the logic to randomize booth placements.
    /// </summary>
    class EventDayViewModel : ObservableObject
    {

        public EventDayViewModel()
        {
            this.EventDay = new EventDay("TEST DAY");
            this.MapViewModel = new MapViewModel(this.EventDay.BoothMap);
        }
  
        public EventDayViewModel(EventDay day)
        {
            this.EventDay = day;
            this.MapViewModel = new MapViewModel(day.BoothMap);
        }
        public EventDay EventDay { get; set;}
        public MapViewModel MapViewModel { get; internal set; }


        /// <summary>
        /// Randomizes the placement of companies in the boothmap
        /// </summary>
        public void RandomizePlacements()
        {
            if (this.EventDay.Registrations.Count == 0)
                return;
            List<Registration> unassigned = new List<Registration>(this.EventDay.Registrations);
            List<Booth> booths = EventDay.BoothMap.BoothList;
            Random rng = new Random();
            // Unassign Everything
            foreach (Booth b in booths)
            {
                b.Assignment = null;
            }

            foreach (Booth b in booths)
            {
                if (unassigned.Count == 0)
                    break;
                int idx = rng.Next(unassigned.Count - 1);
                Registration toAssign = unassigned[idx];
                b.Assignment = toAssign;
                unassigned.Remove(toAssign);
            }
            RaisePropertyChangedEvent("EventDay");
        }
    }
}
