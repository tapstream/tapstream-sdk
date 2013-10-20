using System;

namespace TapstreamMetrics.Sdk
{
    interface AppEventSource
    {
        event EventHandler OnShow;
    }
}
