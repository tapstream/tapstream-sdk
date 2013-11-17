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
#if TEST_WINPHONE || WINDOWS_PHONE
        string GetDeviceUniqueId(); 
#else
        string GetAppSpecificHardwareId();
#endif
        string GetAppName();
        string GetPackageName();
        Response Request(string url, string data, string method);
    }
}
