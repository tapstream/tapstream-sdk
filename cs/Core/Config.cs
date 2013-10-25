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

		// Optional hardware identifiers that can be provided by the caller
		private string odin1 = null;

		// Set these to false if you do NOT want to collect this data.
#if TEST_WINPHONE || WINDOWS_PHONE
		private bool collectDeviceUniqueId = true;
#else
		private bool collectAppSpecificHardwareId = true;
#endif

		// Set these if you want to override the names of the automatic events sent by the sdk
		private string installEventName = null;
		private string openEventName = null;

		// Unset these if you want to disable the sending of the automatic events
		private bool fireAutomaticInstallEvent = true;
		private bool fireAutomaticOpenEvent = true;

        // These parameters will be automatically attached to all events fired by the sdk
        private IDictionary<string, object> globalEventParams = new Dictionary<string, object>();

		// Properties for the private members above:
		public string Hardware
		{
			get { return hardware; }
			set { hardware = value; }
		}

		public string Odin1
		{
			get { return odin1; }
			set { odin1 = value; }
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
		
		public string InstallEventName
		{
			get { return installEventName; }
			set { installEventName = value; }
		}

		public string OpenEventName
		{
			get { return openEventName; }
			set { openEventName = value; }
		}

		public bool FireAutomaticInstallEvent
		{
			get { return fireAutomaticInstallEvent; }
			set { fireAutomaticInstallEvent = value; }
		}

		public bool FireAutomaticOpenEvent
		{
			get { return fireAutomaticOpenEvent; }
			set { fireAutomaticOpenEvent = value; }
		}

        public IDictionary<string, object> GlobalEventParams
        {
            get { return globalEventParams; }
            set { globalEventParams = value; }
        }
	}
}