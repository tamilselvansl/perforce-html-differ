#  Perforce Diff in HTML Format

P4HTMLDiff.java, can be used to generate perforce file diff in the format of HTML and this HTML file will be stored in local disk for later referal. 

This will be useful at the time of sending text-file/code diff for someone for review in HTML format. 

Pre-requisites: Perforce must be installed and logged-in and it only consider files under "default" change list 

All white space and line-ending diff's will be omitted

# Sample Output

	 Executing P4 command to collect list of files opened under default change list
	//depot/workspace/dev/filename.java #5 - edit default change (ktext)
	List of files for diff [1]: //depot/workspace/dev/filename.java]
	Diffing for file : //depot/workspace/dev/filename.java
	File has been written at c:\pfdiff\ filename.html
