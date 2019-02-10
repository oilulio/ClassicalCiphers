import java.util.HashSet;
import java.util.Set;

public class Codespace
{
// Generic Code/Cipher codespace 

/*
Copyright (C) 2019  S Combes

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

protected String freeSpace;  // The allowable characters we can represent
protected String PTmap;      // The mapping from free to PT
protected String PTspace;    // The allowable PT characters 
protected String CTspace;    // The allowable CT characters 
protected String CTmap;      // The mapping from code to CT
protected String codeSpace;  // The allowable characters once encoded and expanded

// The PTspace/CTspace alphabet mappings are of the form (for example)
// ABCDEFGHIIKLMNOPQRSTUVWXZY (note repeated I, and no J) : if the freeSpace is
// ABCDEFGHIJKLMNOPQRSTUVWXYZ then this shows that I&J of free become merged as J in PT
 
// Useful building blocks
protected static final String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Englist std
protected static final String alphabet="abcdefghijklmnopqrstuvwxyz"; // Englist std
protected static final String NUMBERS="1234567890"; 
protected static final String b64chars=ALPHABET+alphabet+NUMBERS+"=/+";
protected static final String b32chars=ALPHABET+"234567"+"=";
protected static final String ascii85chars;

static {
  StringBuilder temp=new StringBuilder();
  for (int i=33;i<(33+85);i++) temp.append((char)i);
  ascii85chars=temp.toString()+"z";
}

public static final String allBytes;
static {
  StringBuilder temp=new StringBuilder();
  for (int i=0;i<256;i++) temp.append((char)i);
  allBytes=temp.toString();
}
static String nL = System.getProperty("line.separator");

protected static String IJmerge="ABCDEFGHIIKLMNOPQRSTUVWXYZ"; // Note I&J -> J
protected static String QUmerge="ABCDEFGHIJKLMNOPURSTUVWXYZ"; // Q&U -> U
protected static String YZmerge="ABCDEFGHIJKLMNOPQRSTUVWXYY"; // Y&Z -> Y
protected static String IJandUVWmerge="ABCDEFGHIIKLMNOPQRSTUUUXYZ"; // IJ -> I; UVW->U
protected static String Qless="ABCDEFGHIJKLMNOPRSTUVWXYZ"; // No Q
public enum StockAlphabet 
{ 
CAPITALS(ALPHABET,ALPHABET,ALPHABET,ALPHABET), 
UPPER_LOWER_NUMBERS(ALPHABET+alphabet+NUMBERS,ALPHABET+alphabet+NUMBERS,ALPHABET+alphabet+NUMBERS,ALPHABET+alphabet+NUMBERS), 
MERGED_IJ(ALPHABET,IJmerge,IJmerge,ALPHABET), 
MERGED_QU(ALPHABET,QUmerge,QUmerge,ALPHABET),
MERGED_YZ(ALPHABET,YZmerge,YZmerge,ALPHABET),
NO_Q(Qless,Qless,Qless,Qless),
MERGED_IJ_UVW(ALPHABET,IJandUVWmerge,IJandUVWmerge,ALPHABET), 
CLASSICAL_LATIN(ALPHABET,IJandUVWmerge,IJandUVWmerge,ALPHABET); // Same as previous

public final String free;
public final String PTmap;
public final String CTmap;
public final String code;

private StockAlphabet(String free,String PTmap,String CTmap,String code) { 
  this.free=free;
  this.PTmap=PTmap;
  this.CTmap=CTmap;
  this.code=code;
}
};
// ----------------------------------------------------------------------
Codespace()       { this(StockAlphabet.CAPITALS); }
// ----------------------------------------------------------------------
Codespace(StockAlphabet thealph) 
   { this(thealph.free,thealph.PTmap,thealph.CTmap,thealph.code); }
// ----------------------------------------------------------------------
Codespace(String alph) // Simplest case
{
this(alph,alph,alph,alph);
if (!unique(alph).equals(alph)) throw new IllegalArgumentException(
  "Alphabet must be unique. "+alph+" has duplicates."); 
}
// ----------------------------------------------------------------------
Codespace(String freeSpace,String PTmap,String CTmap,String codeSpace) 
{ // Worst case - others are inferrable
this.freeSpace=freeSpace;
this.PTmap=PTmap;
this.PTspace=unique(PTmap);
this.CTspace=unique(CTmap);
this.CTmap=CTmap;
this.codeSpace=codeSpace;
}
// ----------------------------------------------------------------------
public String flattenToPT(String text) 
{  // Converts/reduces free text into allowable PT alphabet

StringBuilder sb=new StringBuilder(text.length());
for (int i=0;i<text.length();i++) {
  int j=freeSpace.indexOf(text.charAt(i));
  if (j == (-1)) throw new IllegalArgumentException(
   "Invalid character in text : "+text.charAt(i)+" at position "+i);
  else
    sb.append(PTmap.charAt(j));
}
return sb.toString();
}
// ----------------------------------------------------------------------
public String flattenToCT(String text) 
{  // Converts/reduces code text into allowable CT alphabet

StringBuilder sb=new StringBuilder(text.length());
for (int i=0;i<text.length();i++) {
  int j=codeSpace.indexOf(text.charAt(i));
  if (j == (-1)) throw new IllegalArgumentException(
   "Invalid character in text :"+text.charAt(i)+" at position "+i);
  else
    sb.append(CTmap.charAt(j)); 
}
return sb.toString();
}
// ----------------------------------------------------------------------
public static String unique(String word) {
// Returns the unique letters of word in original order, i.e. 1st occurrence only
 
StringBuilder unique=new StringBuilder(Character.toString(word.charAt(0)));

for (int i=1;i<word.length();i++) 
  if (unique.indexOf(word.substring(i,i+1))==(-1))
    unique.append(Character.toString(word.charAt(i))); 

return unique.toString();
}
// ----------------------------------------------------------------------
public static int uniqueLength(String word) {
// Returns the number of unique letters of word 
 
Set<Character> set=new HashSet<Character>();
char [] ca=word.toCharArray();
for (char c : ca)
  set.add(c);

return set.size();
}
// ----------------------------------------------------------------------
public String toString() {  return (nL+"Codespace using full code space of"+nL+
             freeSpace+" mapping via"+nL+PTmap+" to PT limited to"+nL+
             PTspace+" encoding to CT limited to"+nL+
             CTspace+" mapping via"+nL+CTmap+" to full codespace of"+nL+codeSpace); }

}