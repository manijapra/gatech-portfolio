using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace CareerFairy.Model
{
    /// <summary>
    /// Model for a career fair event.
    /// This class is the source of all of the data for the views and viewmodels
    /// </summary>
    [DataContract(IsReference = true)]
    public class CareerFair
    {
        [DataMember]
        public List<EventDay> Days { get; set; }
        [DataMember]
        public string Name { get; set; }

        public CareerFair()
        {
            Days = new List<EventDay>();
        }
    }
}
