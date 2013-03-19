using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public sealed class Config
    {
        // Deprecated, hardware-id field
        private string hardware = null;

        // Set these to false if you do NOT want to collect this data.
#if TEST_WINPHONE || WINDOWS_PHONE
        private bool collectDeviceUniqueId = true;
#else
        private bool collectAppSpecificHardwareId = true;
#endif

        public string Hardware
        {
            get { return hardware; }
            set { hardware = value; }
        }

#if TEST_WINPHONE || WINDOWS_PHONE
        public bool CollectDeviceUniqueId
        {
            get { return collectDeviceUniqueId; }
            set { collectDeviceUniqueId = value; }
        }
#else
        public bool CollectAppSpecificHardwareId
        {
            get { return collectAppSpecificHardwareId; }
            set { collectAppSpecificHardwareId = value; }
        }
#endif
    }
}