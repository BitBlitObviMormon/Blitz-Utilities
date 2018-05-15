;CONSTANTS
Const FPS = 30	;Used by the update GUI

;GLOBALS
Global totalSize = 0
Global currentSize = 0
Global timer = 0

;TYPES
Type Extra ;For send keys and other functions I might make in the future.
	Field x, ex$ ;These variables hold... something. It depends on the function.
End Type

;PROGRAM
AppTitle("JCrypt")
If Trim$(CommandLine$()) = ""
	file$ = RequestFile$("Choose a file to encrypt/decrypt") ;Request a file
Else
	file$ = Trim$(Replace$(CommandLine$(),Chr$(34),"")) ;Get the file from the command line
End If
If FileType(file$) = 1 Then
	If Lower$(GetExtension$(file$)) = "jcrypt" Then
		Print "Decrypting, Please Wait..."
		SmartDecrypt(file$)
	Else
		Print "Encrypting, Please Wait..."
		SmartEncrypt(file$)
	End If
End If

;FUNCTIONS
;This encrypts a file without the need of a specified bitmask, however, you must use SmartDecrypt to decrypt it.
Function SmartEncrypt(file$)
	;Initialize the GUI
	InitGUI(file$, GetFileSize(file$))

	;Initialize the bitmask
	SeedRnd(MilliSecs())
	bitmask = Rand(0, 255)

	;Open the files
	n1 = ReadFile(file$)
	n2 = WriteFile(Mid$(file$, 1, Len(file$) - Len(GetExtension$(file$))) + "jcrypt")

	;Encrypt the file
	If n1 <> 0 And n2 <> 0 Then
		ext$ = GetExtension$(file$)
		WriteLine(n2, ext$)
		WriteByte(n2, bitmask)
		While Not Eof(n1)
			byte = ReadByte(n1)
			WriteByte(n2,byte Xor bitmask)

			;Update the GUI
			currentSize = currentSize + 1
			UpdateGUI()
		Wend

		;Close the files and free the GUI
		CloseFile(n1)
		CloseFile(n2)
		FreeGUI()
	Else
		RuntimeError("Error: Could not open file.")
	EndIf
End Function

;This decrypts a smart encrypted file without the need of a specified bitmask.
Function SmartDecrypt(file$)
	;Initialize the GUI
	InitGUI(file$, GetFileSize(file$))

	;Open the file
	n1 = ReadFile(file$) ;Open the file.

	;Decrypt the file
	If n1 <> 0 Then
		n2 = WriteFile(Mid$(file$,1,Len(file$) - Len(GetExtension$(file$))) + ReadLine$(n1)) ;Get the extension and write the file
		bitmask = ReadByte(n1) ;Read the first byte to get the bitmask
		While Not Eof(n1)
			byte = ReadByte(n1)
			WriteByte(n2,byte Xor bitmask)

			;Update the GUI
			currentSize = currentSize + 1
			UpdateGUI()
		Wend

		;Close the files and free the GUI
		CloseFile(n1) ;Close the files.
		CloseFile(n2)
		FreeGUI()
	Else
		RuntimeError("Error: Could not open file.")
	EndIf
End Function

;Initializes the GUI
Function InitGUI(file$, total)
	totalSize = total
	timer = CreateTimer(FPS)
End Function

;Called whenever the encryption updates
Function UpdateGUI()
	If (TimerTicks(timer) > 0 Or currentSize = totalSize) Then
		ResetTimer(timer)
		Write(Chr$(13) + Str$(currentSize) + "B / " + Str$(totalSize) + "B (" + (currentSize * 100) / totalSize + "%)")
	EndIf
End Function

;Uninitializes the GUI
Function FreeGUI()
	FreeTimer(timer)
End Function

;Returns the size of a file in bytes without reading the whole file
Function GetFileSize(file$)
	bank = CreateBank(12)
	hndl = kernel_OpenFile(file$, bank, 0)
	i = kernel_GetFileSize(hndl, 0)
	kernel_CloseHandle(hndl)
	Return i
End Function

;This gets the filename of a path, courtesy to Nebula for the code
;http://www.blitzbasic.com/codearcs/codearcs.php?code=758
Function GetFilename$(filename$) ; Returns the filename and extension
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "\" Then Lastdir = i
	Next
	If Lastdir > 1 Then Lastdir = Lastdir + 1
	For i=Lastdir To Len(filename$)
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function

;This gets the extension name of a file, courtesy to Nebula for the code
;http://www.blitzbasic.com/codearcs/codearcs.php?code=758
Function GetExtension$(filename$) ; Returns the extension minus the .
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "." Then Lastdir = i
	Next
	If Lastdir > 1 Then Lastdir = Lastdir + 1
	For i=Lastdir To Len(filename$)
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function