using System;
using System.Collections.Generic;
using System.Drawing;
using System.Runtime.Serialization;
using System.Xml.Serialization;

namespace CareerFairy.Model
{
    /// <summary>
    /// The boothmap class represents a map for a day of the fair
    /// A boothmap has a list of booths associated with it and a baseImage which is currently fixed to the default CRC
    /// </summary>
    [DataContract(IsReference = true)]
    public class BoothMap
    {
        private List<Booth> boothList;
        [DataMember]
        public Uri BaseImagePath { get; internal set; }
        [DataMember]
        public List<Booth> BoothList
        {
            get; set;
        }

        public BoothMap()
        {
            this.BaseImagePath = new Uri(@"Resources/empty-map.png", UriKind.Relative);
            BoothList = new List<Booth>();
        }
    }
}