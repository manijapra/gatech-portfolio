using System.Runtime.Serialization;

namespace CareerFairy.Model
{
    /// <summary>
    /// Represents a registration or a company attending one of the EventDays of the CareerFair
    /// </summary>
    [DataContract(IsReference = true)]
    public class Registration
    {
        [DataMember]
        private Booth _assignment;
        [DataMember]
        public EventDay DayAttending { get; set; }
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public int NumBooths { get; set; }
        [DataMember]
        public Booth Assignment
        {
            get
            {
                return _assignment;
            }
            set
            {
                _assignment = value;
                if (value != null && value.Assignment != this)
                    value.Assignment = this;
            }
        }

        public string BoothName
        {
            get
            {
                if (this.Assignment != null)
                {
                    return this.Assignment.Name;
                }
                return "";
            }
            set
            {
                string name = value;
                foreach (var booth in this.DayAttending.BoothMap.BoothList)
                {
                    if (booth.Name == name)
                    {
                        this.Assignment = booth;
                    }
                }
            }
        }

    }
}