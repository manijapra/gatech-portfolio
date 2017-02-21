using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Windows;

namespace CareerFairy.Model
{
    /// <summary>
    /// Class represents underlying semantics of a booth at a day of the fair on the boothmap
    /// </summary>
    [DataContract(IsReference = true)]
    public class Booth
    {
        [DataMember]
        private Registration _assignment;
        [DataMember]
        public string Name { get; set; }
        [DataMember]
        public Point Position { get; set; }

        /// <summary>
        /// Unused currently, in a future update it will be used to enable rotation of booths on the map
        /// </summary>
        [DataMember]
        public int Rotation { get; set; }

        /// <summary>
        /// The Assignment is the company that is currently assigned to the booth
        /// When updating the assignment the booth will update the registrations assignment so that they match
        /// </summary>
        [DataMember]
        public Registration Assignment
        {
            get
            {
                return this._assignment;
            }
            set
            {
                if (this._assignment != null && this._assignment != value)
                {
                    this._assignment.Assignment = null;
                }
                this._assignment = value;
                if (this._assignment != null)
                    this._assignment.Assignment = this;
            }
        }

        /// <summary>
        /// Creates a new booth with a given name and position
        /// </summary>
        /// <param name="name">The name that will be given to the booths</param>
        /// <param name="topLeft">The top left corner of the booth in x,y coordinates </param>
        public Booth(string name, Point topLeft)
        {
            Name = name;
            Position = topLeft;
        }
    }
}