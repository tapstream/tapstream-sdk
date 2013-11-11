using System;

#if WINDOWS_PHONE
using Newtonsoft.Json.Linq;
#else
using Windows.Data.Json;
#endif


namespace TapstreamMetrics.Sdk
{
    public interface ConversionListener
    {
#if WINDOWS_PHONE
        void ConversionInfo(JArray info);
#else
        void ConversionInfo(JsonArray info);
#endif
    }
}
