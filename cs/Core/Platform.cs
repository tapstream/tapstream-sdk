using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    interface Platform
    {
        string LoadUuid();
        HashSet<string> LoadFiredEvents();
        void SaveFiredEvents(HashSet<string> firedEvents);
        string GetResolution();
        string GetManufacturer();
        string GetModel();
        string GetOs();
        string GetLocale();
        Response Request(string url, string data);
    }
}
