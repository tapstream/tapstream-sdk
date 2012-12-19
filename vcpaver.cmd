if "%VCINSTALLDIR%" == "" (
	call "%VS110COMNTOOLS%\..\..\VC\vcvarsall.bat" x86
)
paver %1 %2 %3 %4 %5 %6 %7 %8 %9