using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TapstreamMetrics.Sdk
{
    class PlatformImpl : Platform
    {
        public Response response = new Response(200, null, null);
        public HashSet<string> savedFiredList = null;

        public string LoadUuid()
        {
            return "00000000-0000-0000-0000-000000000000";
        }

        public HashSet<string> LoadFiredEvents()
        {
            return new HashSet<string>();
        }

        public void SaveFiredEvents(HashSet<string> set)
        {
            savedFiredList = new HashSet<string>(set);
        }

        public string GetResolution()
        {
            return "480x960"; ;
        }

        public string GetManufacturer()
        {
            return "TestManfacturer";
        }

        public string GetModel()
        {
            return "TestModel";
        }

        public string GetOs()
        {
            return "TestOs";
        }

        public string GetLocale()
        {
            return "en_US";
        }

#if TEST_WINPHONE || WINDOWS_PHONE
        public string GetDeviceUniqueId()
        {
            return "000000000000000";
        }
#else
        public string GetAppSpecificHardwareId()
        {
            return "000000000000000";
        }
#endif

        public string GetAppName()
        {
            return "Test App";
        }

        public string GetPackageName()
        {
            return "com.test.TestApp";
        }

        public Response Request(string url, string data, string method)
        {
            return response;
        }
    }
}
