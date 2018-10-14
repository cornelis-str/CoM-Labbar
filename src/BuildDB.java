import java.io.*;

public class BuildDB {
    public static void setup() throws IOException {
        String genPath = "/home/cornelis/IdeaProjects/labb1/generate.sh";
        Process p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", genPath}, null);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int LineLength = maxLine();

        //reading
        File file = new File("/home/cornelis/IdeaProjects/labb1/konkordans_tokenizer.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-1"));

        //writing
        File lazyhashtxt = new File("/home/cornelis/IdeaProjects/labb1/lazyhash.txt");
        File konkordansOrd = new File("/home/cornelis/IdeaProjects/labb1/konkordans_ord.txt");
        File konkordansPos = new File("/home/cornelis/IdeaProjects/labb1/konkordans_pos.txt");

        lazyhashtxt.createNewFile();
        konkordansOrd.createNewFile();
        konkordansPos.createNewFile();

        BufferedWriter bwOrd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(konkordansOrd, true), "ISO-8859-1"));
        BufferedWriter bwPos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(konkordansPos, true), "ISO-8859-1"));


        long[] lazyHash = new long[27932];                                                                  //ö*900+ö*30+ö = 27930 + last line of the document.
        for(int i = 0; i < 27932; i++){                                                                     //initiate to -1 to indicate words starting with these three lettes do not exist.
            lazyHash[i] = -1;
        }
        long posOrd = 0;
        long posPos = 0;


        String tmp;
        String previousword = "";
        boolean first = true;
        while((tmp = in.readLine()) != null) {
            String[] wordpos = tmp.split(" ");
            if(wordpos[0].equals(previousword)) {                                                         //add to existing word
                bwPos.write(" " + wordpos[1]);
                posPos += wordpos[1].length() + 1;
            } else if (first){
                previousword = wordpos[0];
                lazyHash[hashVal(wordpos[0])] = posOrd;

                int lengthOfLine = wordpos[0].length() + 1 + String.valueOf(posPos).length();
                bwOrd.write(wordpos[0] + " " + posPos);
                posOrd += LineLength + 1;
                while(lengthOfLine < LineLength){
                    bwOrd.write(" ");
                    lengthOfLine++;
                }

                bwPos.write(wordpos[1]);
                posPos += wordpos[1].length() + 1;

                first = false;
            } else {
                int curr = hashVal((wordpos[0]));
                int prev = hashVal(previousword);

                if(curr != prev){
                    if(curr > 27931 || curr < 0) {
                        System.out.println("Word could not be hashed: " + wordpos[0]);
                    } else {
                        lazyHash[hashVal(wordpos[0])] = posOrd;
                    }
                }
                previousword = wordpos[0];

                int lengthOfLine = wordpos[0].length() + 1 + String.valueOf(posPos).length();
                bwOrd.write("\n" + wordpos[0] + " " + posPos);
                posOrd += LineLength + 1;
                while(lengthOfLine < LineLength){
                    bwOrd.write(" ");
                    lengthOfLine++;
                }

                bwPos.write("\n" + wordpos[1]);
                posPos += wordpos[1].length() + 1;
            }
        }
        lazyHash[27931] = posOrd;

        saveHash(lazyHash);
        bwOrd.close();
        bwPos.close();
    }

    public static int maxLine() throws IOException{
        File file = new File("/home/cornelis/IdeaProjects/labb1/konkordans_tokenizer.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-1"));

        int max = -1;
        String str;
        while((str = in.readLine()) != null){
            if(max < str.length()){
                max = str.length();
            }
        }

        return max;
    }

    private static void saveHash(long[] l) throws IOException {
        FileWriter fw = new FileWriter("/home/cornelis/IdeaProjects/labb1/lazyhash.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        for(long a : l){
            bw.write(a + ""+ "\n");
        }
        bw.close();
    }

    public static int hashVal(String s){
        int x = 0;
        for(int i = 0; i < s.length() && i < 3;  i++) {
           x += charVal(s.charAt(i)) * Math.pow(30,2-i);
        }
        return x;
    }
    private static int charVal(char c){
        //ö = 246, å = 229, ä = 228 när vi använder IO-8859-1
        int[] alfa =  {97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 229, 228, 246};
        for(int i = 0; i < alfa.length; i++){
            if(c == alfa[i]){
                return i + 1;
            }
        }
        System.out.println("Character hashed to 0: " + c);
        return 0;
    }
}
