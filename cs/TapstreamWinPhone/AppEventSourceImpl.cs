using Microsoft.Phone.Shell;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    class AppEventSourceImpl : AppEventSource
    {
        public class PhoneApplicationServiceNotCreated : Exception
        {
            public PhoneApplicationServiceNotCreated(string msg) : base(msg) { }
        }

        public event EventHandler OnShow;

        public AppEventSourceImpl()
        {
            if (PhoneApplicationService.Current == null)
            {
                throw new PhoneApplicationServiceNotCreated("The Tapstream SDK requires the PhoneApplicationService singleton to already been instantiated.  Make sure that you initialize Tapstream *after* the InitializeComponent() call in your application's constructor.");
            }

            PhoneApplicationService.Current.Activated += (Object sender, ActivatedEventArgs args) =>
            {
                if (OnShow != null)
                {
                    OnShow(this, args);
                }
            };
        }
    }
}
