using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace CareerFairy.Model
{
    /// <summary>
    /// This class is a signle day of the career fair
    /// Every event day has a list of companies attending and a boothmap with assignments
    /// </summary>
    [DataContract(IsReference = true)]
    public class EventDay
    {
        [DataMember]
        public BoothMap BoothMap { get; set; }
        [DataMember]
        public List<Registration> Registrations { get; set; }
        [DataMember]
        public string Name { get; set; }

        public EventDay()
        {
            BoothMap = new BoothMap();
            Registrations = new List<Registration>();
            Name = "";
        }

        public EventDay(string name)
        {
            BoothMap = new BoothMap();
            Registrations = new List<Registration>();
            Name = name;
        }
    }
}
