from paver.easy import *
from paver.easy import path
import re
import itertools

"""
Notes:

Java build tasks require ant to be on your path

C# tasks require Visual Studio and might need to be run using the
vcpaver.cmd script which sets up the VC environment variables. (You may
need to adjust the path to vcvarsall.bat in the vcpaver.cmd script)

ObjC tasks require clang.  To build the test application, you also need v8:
To get v8 libraries on OSX:
	get brew
	sudo brew install v8

For packaging sdks on windows, make sure 7zip is on your path.

Building docs requires markdown and django:
pip install markdown django

"""

PRETTY_PLATFORMS = {
	'ios': 'iOS',
	'mac': 'Mac',
	'android': 'Android',
	'win8': 'Windows 8',
	'winphone': 'Windows Phone',
}

DEBUG = False
CONFIGURATION = 'Release'

@task
def debug():
	global DEBUG, CONFIGURATION
	DEBUG = True
	CONFIGURATION = 'Debug'


# Java sdk for Android
def _make_java(path):
	target = CONFIGURATION.lower()
	with pushd(path):
		with pushd('Core'):
			sh('ant %s' % target)
		with pushd('Tapstream'):
			sh('ant %s' % target)
		with pushd('TapstreamTest'):
			sh('ant %s' % target)

def _gen_java_whitelabel():
	path('java-whitelabel').rmtree()
	path.copytree(path('java'), path('java-whitelabel'))
	for d in ('Core', 'Tapstream', 'TapstreamTest'):
		path.move(path('java-whitelabel/%s/src/com/tapstream' % d), path('java-whitelabel/%s/src/com/conversiontracker' % d))
	path.move(path('java-whitelabel/Tapstream/src/com/conversiontracker/sdk/Tapstream.java'), path('java-whitelabel/Tapstream/src/com/conversiontracker/sdk/ConversionTracker.java'))

	package_name = re.compile(r'com\.tapstream\.sdk')
	class_name = re.compile(r'([\s("])Tapstream([\s(.])')
	for file_path in path('java-whitelabel').walkfiles('*.java'):
		with open(file_path, 'rb+') as f:
			data = f.read()
			data = package_name.sub('com.conversiontracker.sdk', data)
			data = class_name.sub(r'\1ConversionTracker\2', data)
			f.seek(0)
			f.write(data)
			f.truncate()

def _package_java():
	path('builds/android').rmtree()
	path('builds/android').makedirs()
	path.copy(path('./java/Tapstream/build/jar/Tapstream.jar'), path('./builds/android/'))
	path.copy(path('./java/Tapstream/build/jar/Tapstream.jar'), path('./examples/Android/Example/libs/'))
	path('builds/TapstreamSDK-android.zip').remove()
	with pushd('builds/android'):
		sh('7z a -tzip ../TapstreamSDK-android.zip Tapstream.jar')

def _package_java_whitelabel():
	path('builds/android-whitelabel').rmtree()
	path('builds/android-whitelabel').makedirs()
	path.copy(path('./java-whitelabel/Tapstream/build/jar/Tapstream.jar'), path('./builds/android-whitelabel/ConversionTracker.jar'))
	path('builds/TapstreamSDK-android-whitelabel.zip').remove()
	with pushd('builds/android-whitelabel'):
		sh('7z a -tzip ../TapstreamSDK-android-whitelabel.zip ConversionTracker.jar')
	path('java-whitelabel').rmtree()

@task
def make_java():
	_make_java('java')
	
@task
def test_java():
	sh('java -jar ./java/TapstreamTest/build/jar/TapstreamTest.jar tests.js')

@task
def package_java():
	_make_java('java')
	_gen_java_whitelabel()
	_make_java('java-whitelabel')
	_package_java()
	_package_java_whitelabel()



# C# sdk for Windows 8 and Windows Phone
@task
def make_cs():
	with pushd('cs'):
		sh('msbuild /m Tapstream.sln /t:Build /p:Configuration=%s' % CONFIGURATION)
		sh('msbuild /m TapstreamWinPhone.sln /t:Build /p:Configuration=%s' % CONFIGURATION)
		# Build win8 test code
		sh('msbuild /m TapstreamTest.sln /t:Build /p:Configuration=Debug;OutDir=bin/Debug/win8')
		# Build winphone test code
		sh('msbuild /m TapstreamTest.sln /t:Build /p:Configuration=Debug;OutDir=bin/Debug/winphone;DefineConstants="DEBUG;TRACE;TEST_WINPHONE"')

@task
def test_cs_win8():
	sh('"cs/TapstreamTest/bin/Debug/win8/TapstreamTest.exe" tests.js')

@task
def test_cs_winphone():
	sh('"cs/TapstreamTest/bin/Debug/winphone/TapstreamTest.exe" tests.js')

@task
def test_cs():
	test_cs_win8()
	test_cs_winphone()

@task
def package_cs():
	make_cs()
	path('builds/win8').rmtree()
	path('builds/win8').makedirs()
	path.copy(path('./cs/Tapstream/bin/%s/TapstreamMetrics.winmd' % CONFIGURATION), path('./builds/win8/'))
	
	path('builds/winphone').rmtree()
	path('builds/winphone').makedirs()
	path.copy(path('./cs/TapstreamWinPhone/Bin/%s/TapstreamMetrics.dll' % CONFIGURATION), path('./builds/winphone/'))

	path('builds/TapstreamSDK-win8.zip').remove()
	with pushd('builds/win8'):
		sh('7z a -tzip ../TapstreamSDK-win8.zip TapstreamMetrics.winmd')

	path('builds/TapstreamSDK-winphone.zip').remove()
	with pushd('builds/winphone'):
		sh('7z a -tzip ../TapstreamSDK-winphone.zip TapstreamMetrics.dll')





# ObjC sdk for Mac and iOS
def listify(series, prefix=''):
	return ' '.join(['%s%s' % (prefix, path(d).normpath().replace('\\', '/')) for d in series])

@task
def make_objc():
	with pushd('objc'):
		path('Tapstream/bin').mkdir()
		path('TapstreamTest/bin').mkdir()

		include_dirs = listify(['Core', '/usr/local/include/'], prefix='-I')

		core_sources = listify(path('Core').walkfiles('*.m'))
		tapstream_sources = ' '.join((
			core_sources,
			listify(path('Tapstream').walkfiles('*.m'))
		))
		tapstream_test_sources = ' '.join((
			core_sources,
			listify(path('TapstreamTest').walkfiles('*.m')),
			listify(path('TapstreamTest').walkfiles('*.mm'))
		))

		# Compile the non-test code as well to catch potential errors (even though this sdk will be distributed as source)

		# iOS With and without ARC
		clang = sh('echo xcrun -sdk iphoneos clang', capture=True).strip()
		sdk_root = sh('echo $(xcodebuild -version -sdk iphoneos Path)', capture=True).strip()
		sh('%s -isysroot %s -miphoneos-version-min=4.3 -arch armv7 -fno-objc-arc -shared %s %s -o ./TapstreamTest/bin/Tapstream.so -framework Foundation -framework UIKit' % (
			clang, sdk_root, include_dirs, tapstream_sources
		))
		sh('%s -isysroot %s -miphoneos-version-min=4.3 -arch armv7 -fobjc-arc -shared %s %s -o ./TapstreamTest/bin/Tapstream.so -framework Foundation -framework UIKit' % (
			clang, sdk_root, include_dirs, tapstream_sources
		))
		
		# Mac With and without ARC
		sh('clang -fno-objc-arc -shared %s %s -o ./TapstreamTest/bin/Tapstream.so -framework Foundation -framework AppKit' % (
			include_dirs, tapstream_sources
		))
		sh('clang -fobjc-arc -shared %s %s -o ./TapstreamTest/bin/Tapstream.so -framework Foundation -framework AppKit' % (
			include_dirs, tapstream_sources
		))

		# Compile test application for ios
		sh('clang++ -fobjc-arc %s %s -o ./TapstreamTest/bin/TapstreamTestIos -DTEST_IOS=1 -DTEST_PLATFORM=ios -lv8 -framework Foundation' % (
			include_dirs, tapstream_test_sources
		))
		# Compile test application for mac
		sh('clang++ -fobjc-arc %s %s -o ./TapstreamTest/bin/TapstreamTestMac -DTEST_PLATFORM=mac -lv8 -framework Foundation' % (
			include_dirs, tapstream_test_sources
		))
		

@task
def test_objc_mac():
	sh('./objc/TapstreamTest/bin/TapstreamTestMac tests.js')

@task
def test_objc_ios():
	sh('./objc/TapstreamTest/bin/TapstreamTestIos tests.js')

@task
def test_objc():
	test_objc_mac()
	test_objc_ios()

@task
def package_objc():
	make_objc()
	path('builds').mkdir()
	for sdk in ('mac', 'ios'):
		path('builds/%s' % sdk).rmtree()
		path('builds/%s/Tapstream' % sdk).makedirs()
		for file_type in ('.h', '.m'):
			sh('cp ./objc/Tapstream/*%s ./builds/%s/Tapstream/' % (file_type, sdk))
			sh('cp ./objc/Core/*%s ./builds/%s/Tapstream/' % (file_type, sdk))
		path('builds/TapstreamSDK-%s.zip' % sdk).remove()
		with pushd('builds/%s' % sdk):
			sh('zip -r ../TapstreamSDK-%s.zip Tapstream' % sdk)

		# Generate whitelabel
		path('builds/%s-whitelabel' % sdk).rmtree()
		path('builds/%s-whitelabel' % sdk).mkdir()
		sh('cp -r ./builds/%s/Tapstream ./builds/%s-whitelabel/' % (sdk, sdk))
		with pushd('./builds/%s-whitelabel' % sdk):
			sh('mv Tapstream ConversionTracker')
			sh('mv ConversionTracker/TSTapstream.h ConversionTracker/ConversionTracker.h')
			sh('mv ConversionTracker/TSTapstream.m ConversionTracker/ConversionTracker.m')

			pattern = re.compile(r'(With|[\s\[("])(?:TS)?Tapstream([\s(.*:])')
			for file_path in itertools.chain(path('.').walkfiles('*.h'), path('.').walkfiles('*.m')):
				with open(file_path, 'rb+') as f:
					data = f.read()
					data = pattern.sub(r'\1ConversionTracker\2', data)
					f.seek(0)
					f.write(data)
					f.truncate()

		path('builds/TapstreamSDK-%s-whitelabel.zip' % sdk).remove()
		with pushd('builds/%s-whitelabel' % sdk):
			sh('zip -r ../TapstreamSDK-%s-whitelabel.zip ConversionTracker' % sdk)


@task
def docs():
	import markdown
	from django.template import Context, Template
	from django.conf import settings
	settings.configure()

	path('builds/docs').rmtree()
	path('builds/docs').makedirs()
	path.copy(path('./docs/bootstrap.min.css'), path('./builds/docs/'))
	path.copy(path('./docs/pygments.css'), path('./builds/docs/'))

	with open('docs/base.html') as f:
		base_template = Template(f.read())
	with open('docs/docs.md') as f:
		md_template = Template(f.read())
	with pushd('builds/docs'):
		for platform in ('android', 'ios', 'mac', 'win8', 'winphone'):
			md = md_template.render(Context({'platform': platform, 'pretty_platform': PRETTY_PLATFORMS[platform]}))
			with open('docs_%s.md' % platform, 'w') as f:
				f.write(md)
			md = markdown.markdown(md, ['fenced_code', 'codehilite'])
			page = base_template.render(Context({'md': md}))
			with open('docs_%s.html' % platform, 'w') as f:
				f.write(page)