;TYPES
Type drive
	Field a$,c
End Type

b = 98
Global d.drive = New drive
d\a$ = Upper$(Chr$(97))
d\c = FileType(Str$(d\a$ + ":"))
While b <= 122
	d.drive = New drive
	d\a$ = Upper$(Chr$(b))
	d\c = FileType(Str$(d\a$ + ":"))
	b = b + 1
Wend
font = LoadFont("arial",14)
w = GadgetWidth(Desktop())
h = GadgetHeight(Desktop())
Global alarm_mode = Proceed("Want alarm mode on?",0)
If alarm_mode <> 1 Then alarm_mode = False
If alarm_mode = False Then
	If Proceed("Do you want to exit the program?",0) = True Then End
End If
window = CreateWindow("Type in drive and password",w/2,h/2,115,112,0,3)
textbar = CreateTextField(0,0,65,24,window,0)
passwordbar = CreateTextField(0,24,65,24,window,1)
button = CreateButton("OK",65,0,50,24,window,4)
SetGadgetFont(textbar,font)
SetGadgetFont(passwordbar,font)
SetGadgetText(textbar,"F:")
Repeat
	TestDrives()
	If WaitEvent(100)=$401 Then
		If EventSource()=button Then Exit
	End If
Forever
Global drive$ = Upper$(Replace$(Replace$(Replace$(TextFieldText$(textbar),"\",""),"/","")," ",""))
password$ = TextFieldText$(passwordbar)
FreeGadget(window)
window = CreateWindow("Press to eject drive!",0,0,115,24,0,0)
button = CreateButton("Press to eject drive!",0,0,115,24,window,0)
Repeat
	TestDrives()
	If WaitEvent(100)=$401 Then
		If EventSource()=button Then Exit
	End If
Forever
FreeGadget(window)
window = CreateWindow("Password?",w/2,h/2,115,60,0,3)
passwordbar = CreateTextField(0,0,65,24,window,1)
SetGadgetFont(passwordbar,font)
button = CreateButton("OK",65,0,50,24,window,4)
.password
Repeat
	TestDrives()
	If WaitEvent(100)=$401 Then
		If EventSource()=button Then Exit
	End If
Forever
If TextFieldText$(passwordbar) = password$ Then
	EjectDrive(Upper$(drive$))
	End
Else
	FreeGadget(window)
	window = CreateWindow("Incorrect.",w/2,h/2,115,60,0,3)
	passwordbar = CreateTextField(0,0,65,24,window,1)
	button = CreateButton("OK",85,0,50,24,window,4)
	Goto password
End If

;FUNCTIONS
Function TestDrives()
	For d.drive = Each drive
		If d\c <> FileType(Str$(d\a$ + ":")) Then
			d\c = FileType(Str$(d\a$ + ":"))
			Select d\c
				Case 2
					Notify("Drive " + d\a$ + ": was inserted.") ;Display notification
				Case 0
					If alarm_mode = True And d\a$ + ":" = drive$ Then
						SystemBeep(500,50) ;Give a 3-beep alarm.
						Delay 50
						SystemBeep(500,50)
						Delay 50
						SystemBeep(500,50)
						Delay 100
						Notify("Drive " + d\a$ + ": was removed!") ;Display notification
					Else
						Notify("Drive " + d\a$ + ": was removed.") ;Display notification
					End If
				Default
					Notify("What the hay??")
			End Select
		End If
	Next
End Function

Function EjectDrive(drive$)
	;This does nothing right now.
End Function