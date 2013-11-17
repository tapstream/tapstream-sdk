using System;

namespace TapstreamMetrics.Sdk
{
    public interface ConversionListener
    {
        void ConversionInfo(string jsonInfo);
    }
}
