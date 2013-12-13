using System;

namespace TapstreamMetrics.Sdk
{
#if WINDOWS_PHONE
    public interface ConversionListener
    {
        void ConversionData(string jsonInfo);
    }
#endif
}
