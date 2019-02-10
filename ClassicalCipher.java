import java.util.List;
import java.util.ArrayList;

public class ClassicalCipher extends Encoding
{
// Generic Cipher utilities and base class for other ciphers 

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

// ----------------------------------------------------------------------
ClassicalCipher()                      { super();   }
ClassicalCipher(Codespace cs)          { super(cs); }
// ----------------------------------------------------------------------
public ClassicalCipher copyInstance(ClassicalCipher c) // Copy factory method
  { return new ClassicalCipher(c.cs); }
// ----------------------------------------------------------------------
public String encode(String text) { return text; } // Functions as null cipher
public String decode(String text) { return text; } // Ditto 
// ----------------------------------------------------------------------
public String seriateOn(String text,int rank) 
{
StringBuilder sb=new StringBuilder();

int therank=rank; // start here
int x0=0; // ditto

while (x0<text.length()) {
  int numberLeft=text.length()-x0;
  if (numberLeft < therank*2) therank=(numberLeft+1)/2;
  
  for (int r=0;r<therank;r++) {
    sb.append(text.charAt(x0+r));  
    if (x0+r+therank < text.length())
      sb.append(text.charAt(x0+r+therank));  
  }
  x0+=(2*therank);
}
return sb.toString();
}
// ----------------------------------------------------------------------
public String seriateOff(String text,int rank)
{
StringBuilder sb=new StringBuilder();

int therank=rank; // start here
int x0=0; // ditto

while (x0<text.length()) {
  int numberLeft=text.length()-x0;
  if (numberLeft < therank*2) therank=(numberLeft+1)/2;
  
  for (int r=0;r<therank*2;r+=2) 
    sb.append(text.charAt(x0+r));
  for (int r=1;r<therank*2;r+=2) 
    if ((x0+r) < text.length())
      sb.append(text.charAt(x0+r));  
  x0+=(2*therank);
}
return sb.toString();
}
// ----------------------------------------------------------------------
protected String regularise(String text,int rank,String nulls) {
// Pads a string to make a columnar cipher regular

if ((text.length()%rank)==0) return text;

StringBuilder reg=new StringBuilder(rank*(1+text.length()/rank));

reg.append(text);
for (int i=0;i<(rank-(text.length()%rank));i++)
  reg.append(nulls.charAt(rand.nextInt(nulls.length()))); 

return (reg.toString());
}
// ----------------------------------------------------------------------
protected String pad(String word) {
// Pads a string to alphabet chars with unique letters in alphabetic order

StringBuilder pad=new StringBuilder();

for (int i=0;i<cs.PTspace.length();i++) 
  if (!word.contains(Character.toString(cs.PTspace.charAt(i))))
    pad.append(Character.toString(cs.PTspace.charAt(i))); 

return (pad.insert(0,word).toString());
}
// ----------------------------------------------------------------------
protected String dap(String word) {
// Pads a string to alphabet chars with unique letters in reverse 
// alphabetic order (i.e. reverse of pad)

StringBuilder pad=new StringBuilder();

for (int i=0;i<cs.PTspace.length();i++) 
  if (!word.contains(Character.toString(cs.PTspace.charAt(cs.PTspace.length()-1-i))))
    pad.append(Character.toString(cs.PTspace.charAt(cs.PTspace.length()-1-i))); 

return (pad.insert(0,word).toString());
}// ----------------------------------------------------------------------
protected String padnull(String word) {
// Pads a string to alphabet chars with unique letters in alphabetic order 
// and nulls in place of repeated letters

StringBuilder pad=new StringBuilder();

for (int i=0;i<cs.PTspace.length();i++) 
  if (!word.contains(Character.toString(cs.PTspace.charAt(i))))
    pad.append(Character.toString(cs.PTspace.charAt(i))); 
  else
    pad.append("_");

return (pad.insert(0,word).toString());
}
// ----------------------------------------------------------------------
public static String map(String text,int [] transpose) 
{
// Example input : text ABCDE and order 42013, produces ECABD
// Inverting 42013 gives 23140
// Transposition cipher generic.  When provided by the array that dictates
// that text2 1stletter in nth position to text2 letter m, construct text2
// Forces exact length match, otherwise same as permute.

if (text.length() != transpose.length) System.out.println("Length Mismatch");

return permute(text,transpose);
}
// ----------------------------------------------------------------------
public static String permute(String text,int [] transpose) 
{
// Permutes the text string using the transpose array, repeats if 
// text is longer than transpose.

// TODO : If not integer multiples, currently potentially shortens string
// because cannot map from beyond end
// Not obvious how to work if not multiple.

StringBuilder result=new StringBuilder(text.length());

for (int index=0;index<text.length();index++) {
  int ti=transpose.length*(index/transpose.length)+
                 transpose[index%transpose.length];
  if (ti<text.length())
    result.append(Character.toString(text.charAt(ti)));
}
return result.toString();
}
// ----------------------------------------------------------------------
public static int[] getMap(int length,int [] order) 
{ 
// Given a text of length 'length', return the mapping between original chars
// and new chars (new = transpose[old]) constructed by placing text in as many
// columns as are in the order array, and reading off from top to bottom of the 
// columns in the order specified by order[].  

// Constraints : order contains 0 to n-1 in some order.  No repeats/omissions
// Assumes a regular array.

int [] transpose=new int[length];

int serial=0;
int ncol=order.length;
int nrow=(int)((length-1)/ncol)+1;
for (int col=0;col<ncol;col++)
  for (int row=0;row<nrow;row++) {
//???      int index=(nrow-1-row)*ncol+order[col];
    int index=row*ncol+order[col];
    if (index < length)
      transpose[index]=serial++;
  }
return transpose;
}
// ----------------------------------------------------------------------
public static String invertByCols(String text,int [] order) 
{ // Keyed columnar transposition
int [] transpose=getMap(text.length(),order);
return map(text,transpose); // map is the 'inverted' version anyway
}
// ----------------------------------------------------------------------
public static String byCols(String text,int [] order) 
{ // Inverted Keyed columnar transposition

int [] transpose=getMap(text.length(),order);

return map(text,invert(transpose));
}
// ----------------------------------------------------------------------
public static int[] invert(int[] list) 
{ // Inverts a mapping.

int [] inv=new int[list.length];

for (int i=0;i<list.length;i++) 
  inv[list[i]]=i;

return (inv);
}
// ----------------------------------------------------------------------
public int[] orderit(String word) 
{ // Returns array keyed by order of letters with duplicates leading to
  // same numbers.  i.e. ACB returns [0,2,1]; ACCB is [0,2,2,1]
  // May sometimes be helpful to call with words with unique letters (use unique())
  // see also oneorderit()

int [] result=new int[word.length()];

int place=0;
for (int i=0;i<cs.PTspace.length();i++) 
  if (word.contains(Character.toString(cs.PTspace.charAt(i)))) {
    for (int j=0;j<word.length();j++) 
      if (word.charAt(j)==cs.PTspace.charAt(i))
        result[j]=place;
    place++;
  }
return result;
}
// ----------------------------------------------------------------------
public int[] oneorderit(String word) 
{ // Returns array keyed by order of letters, with duplicates leading to
  // consecutive numbers, i.e. all numbers are unique. e.g. ACB returns [0,2,1];
  // ACCB is [0,2,3,1]
  // May sometimes be helpful to use with words with unique letters.  See also orderit()

int [] result=new int[word.length()];

int place=0;
for (int i=0;i<cs.PTspace.length();i++) 
  if (word.contains(Character.toString(cs.PTspace.charAt(i)))) {
    for (int j=0;j<word.length();j++) 
      if (word.charAt(j)==cs.PTspace.charAt(i)) {
        result[j]=place++;
      }
  }

return result;
}
// ----------------------------------------------------------------------
public static String uniquenull(String word) {
// Returns the unique letters of word in original order, with nulls ("_")
// in the repeat positions

StringBuilder unique=new StringBuilder(Character.toString(word.charAt(0)));

for (int i=1;i<word.length();i++) 
  if (unique.indexOf(word.substring(i,i+1))==(-1))
    unique.append(Character.toString(word.charAt(i))); 
  else
    unique.append("_"); 

return unique.toString();
}
// ----------------------------------------------------------------------
public String getCanonical(String word) {
// Returns a canonical monoalphabetic representation by replacing letters 
// of word with ordered letters of PTspace. e.g. TEST = ABCA

StringBuilder canonical=new StringBuilder(word.length());

int index=0;
for (int i=0;i<word.length();i++) {

  int place=word.substring(0,i).indexOf(word.substring(i,i+1));
  if (place==(-1))
    canonical.append(Character.toString(cs.PTspace.charAt(index++))); 
  else 
    canonical.append(canonical.substring(place,place+1)); 
}
return canonical.toString();
}
// ----------------------------------------------------------------------
public int [] getCanonicalInt(String word) {
// Returns a canonical monoalphabetic representation by replacing letters 
// of word with ordered numbers e.g. TEST = [0,1,2,0]

int [] canonical=new int[word.length()];

int index=0;
for (int i=0;i<word.length();i++) {

  int place=word.substring(0,i).indexOf(word.substring(i,i+1));
  if (place==(-1))
    canonical[i]=index++; 
  else 
    canonical[i]=canonical[place]; 
}
return canonical;
}
}