package com.tamil.p4diff;

/**
 * @author : Tamilselvan Teivasekamani
 * @Date : 19-Nov-2015
 *
 * P4HTMLDiff.java, can be used to generate perforce file diff in the format of HTML and this HTML file will be stored in local disk for later referal.
 * This will be useful at the time of sending text-file/code diff for someone for review in HTML format.
 *
 * Pre-requisites: Perforce must be installed and logged-in and it only consider files under "default" change list
 *
 * All white space and line-ending diff's will be omitted
 */

import java.io.*;
import java.util.HashSet;

public class P4HTMLDiff {
    HashSet<String> filesList;
    private String outputfolder = "c:\\pfdiff\\";

    P4HTMLDiff() {
        File dir = new File(outputfolder);
        if (!dir.exists()) dir.mkdirs();
        this.filesList = new HashSet<String>();
        try {
            executeP4Command();
            formatDiff();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeP4Command() throws IOException {
        System.out.println("Executing P4 command to collect list of files opened under default change list");
        ProcessBuilder processBuilder = new ProcessBuilder("p4", "opened" , "-c", "default");// By default look for diff under "default" change list
        Process process = processBuilder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        InputStreamReader est = new InputStreamReader(process.getErrorStream());
        BufferedReader brErr = new BufferedReader(est);
        String line;
        do {
            line = br.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
            filesList.add(getFileName(line));

        } while(true);

        while ((line = brErr.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        brErr.close();
        System.out.println("List of files for diff ["+filesList.size()+"]: " + filesList);
    }

    private void formatDiff() throws Exception {
        boolean isError = false;
        for (String aFile : filesList) {
            System.out.println("Diffing for file : " + aFile);
            ProcessBuilder processBuilder = new ProcessBuilder("p4", "diff" , "-dc[100000]ubw", aFile);
            Process process = processBuilder.start();
            InputStream is = process.getInputStream();
            InputStreamReader est = new InputStreamReader(process.getErrorStream());
            BufferedReader brErr = new BufferedReader(est);

            String line;
            while (brErr.ready() && (line = brErr.readLine()) != null) {
                System.out.println(line);
                isError = true;
            }
            if (!isError) {
                String content = formatDiff(is);
                String fileName = aFile.substring(aFile.lastIndexOf("/")+1, aFile.lastIndexOf("."));
                saveHTMLDiffToFile(content, outputfolder + fileName + ".html");
            }

            brErr.close();
        }
    }

    private String formatDiff(InputStream in) throws Exception {
        StringBuilder output = new StringBuilder(8192), formattedOutput = new StringBuilder(8192);
        output.append("\n<html>\n<head>\n");
        output.append("<style>\n");
        output.append("pre {line-height: 3px;}\n");
        output.append(".lhs-removed{text-decoration: line-through; background-color: #fdc6c6;line-height: 15px;}\n");
        output.append(".rhs-added{background-color: #ccffcc;line-height: 15px;}\n");
        output.append("</style>\n");
        output.append("</head>\n<body>\n<table width=\"100%\">");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        do {
            line = reader.readLine();
            if (line == null) break;
            if (line.startsWith("-")) {
                line = "<pre class=\"lhs-removed\">" + line + "</pre>\n";
            } else if (line.startsWith("+")) {
                line = "<pre class=\"rhs-added\">" + line + "</pre>\n";
            } else {
                line = "<pre>" + line + "</pre>\n";
            }
            formattedOutput.append(line);
        } while(true);

        output.append(formattedOutput.toString());
        output.append("</table>\n</body>\n</html>");
        reader.close();
//        System.out.println("HTML Output: " + output.toString());
        return output.toString();
    }

    private void saveHTMLDiffToFile(String contents, String fileName) {
        BufferedWriter bos = null;
        File aFile = new File(fileName);
        try {
            if (aFile.exists()) aFile.delete();
            bos = new BufferedWriter(new FileWriter(aFile));
            bos.write(contents);
            System.out.println("File has been written at " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bos) try {bos.close();} catch (IOException e) {e.printStackTrace();}
        }
    }

    private String getFileName(String rawFileName) {
        return rawFileName.substring(rawFileName.indexOf("//"), rawFileName.indexOf("#"));
    }

    public static void main(String[] args) {
        new P4HTMLDiff();
    }
}
