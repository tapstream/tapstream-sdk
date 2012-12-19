using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tapstream.Sdk
{
    class PlatformImpl : Platform
    {
        public Response response = new Response(200, null);
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

        public Response Request(string url, string data)
        {
            return response;
        }
    }
}
