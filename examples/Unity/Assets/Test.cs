using UnityEngine;
using System.Collections;

public class Test : MonoBehaviour {

	// Use this for initialization
	void Start () {
		Debug.Log("----------------------\n\n\n\nRunning Test.Start Now\n\n\n\n-------------------------");
		Tapstream.Config conf = new Tapstream.Config();
		conf.Set("fireAutomaticInstallEvent", false);
		conf.Set("openEventName", "unity open");
		Tapstream.Create("sdktest", "YGP2pezGTI6ec48uti4o1w", conf);
		
		Tapstream.Event e = new Tapstream.Event("test-event", false);
		e.AddPair("level", 5);
		e.AddPair("name", "john doe");
		e.AddPair ("score", 10.6);
		Tapstream.FireEvent(e);

		Tapstream.GetConversionData("GameObject", "HandleGetConversion");
	}
	
	// Update is called once per frame	
	void Update () {
	
	}

	void HandleGetConversion(string jsonSerializedConversionData)
	{
		Debug.Log("Handling Conversion Data...");
		if (jsonSerializedConversionData != null) {
			Debug.Log("Data exists!");
			for(int ii = 0; ii < jsonSerializedConversionData.Length; ii += 500){
				Debug.Log (jsonSerializedConversionData.Substring(ii));
			}
		}
	}
}
