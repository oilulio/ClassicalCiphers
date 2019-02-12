public class ElsieFour extends ClassicalCipher
{
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
    
// Implements ElsieFour cipher per Kaminsky, A. (2017). ElsieFour:
// A Low-Tech Authenticated Encryption Algorithm For Human-to-Human
// Communication Available at: https://eprint.iacr.org/2017/339.pdf

// Uses naming convention based on that paper :
// [Noting that the key mutates so all these have different values at
// each character in the text]
// (i,j) : (row,col) of location of the marker in the grid 
//         i*6+j = marker = ps (pointer to s)
// (x,y) : (row,col) of location of the PT element in the grid 
//         x*6+y = ppt (pointer to PT in grid)
// (r,c) : (row,col) of location of the CT element in the grid
//         r*6+c = pct (pointer to CT in grid)

//           c  j y
//           ------
//          |......      Showing grid with ...
//         i|...M..      PT (P) and associated indices (r,c)
//         r|P.....      CT (C) and associated indices (x,y)
//          |......  Marker (M) and associated indices (i,j)
//          |......
//         x|.....C      [Note x,y not in usual axes]

// ipt = integer value of PT character.  Hence ipt=ikey[ppt]
// ict = integer value of CT character.  Hence ict=ikey[pct]
// is  = integer value at marker position in grid.  is=ikey[ps]=ikey[marker]
// each ipt[],ict[] can be indexed by place in string

// spt,sct : string of PT/CT characters

public int [] ikey; // key grid containing integers 0-35, or 36 for unknown 
// Normally a permutation, but during cryptanalysis may contain 'unknowns'
protected int marker;  // Cell in which marker is presently (0-35)
protected boolean [] used; // Which key letters already used

final static String keyspace="#_23456789abcdefghijklmnopqrstuvwxyz*"; // wildcard at end

static Codespace efcodespace;
static { efcodespace=new Codespace(keyspace,keyspace,keyspace,keyspace); }
// ----------------------------------------------------------------------
ElsieFour(String key)
{
super(efcodespace);
this.ikey=new int[36];
this.used=new boolean[36];

if (key.length()!=36) throw new
        IllegalArgumentException("Invalid length of key.  Is "+key.length());

if (cs.unique(key).length()!=36) throw new
        IllegalArgumentException("Invalid length of unique key.  Is "+
                                 cs.unique(key).length());

for (int i=0;i<36;i++) {
  ikey[i]=cs.PTspace.indexOf(key.charAt(i));
}
marker=0; // Default start
}
// ----------------------------------------------------------------------
ElsieFour(int [] ikey,int marker) // Can have unknowns
{
super(efcodespace);
this.ikey=new int[36];
this.used=new boolean[36];

for (int i=0;i<36;i++) {
  this.ikey[i]=ikey[i];
  if (ikey[i]!=36) {
    if (used[ikey[i]]) throw new
        IllegalArgumentException("Repetitions in key ");
    used[ikey[i]]=true;
  }
}
this.marker=marker; 
}
// ----------------------------------------------------------------------
public String toString()
{
StringBuilder sb=new StringBuilder(100);

for (int i=0;i<36;i++) {
  sb.append(cs.PTspace.charAt(ikey[i]));
}
return ("ElsieFour cipher. Key : "+sb.toString()+
  " Marker at ("+(marker/6)+","+(marker%6)+")"); // Don't print codespace (default)
}
// ----------------------------------------------------------------------
@Override
public String encode(String PT)
{
String flat=cs.flattenToPT(PT);

StringBuilder sb=new StringBuilder(PT.length());
for (int index=0;index<flat.length();index++) {

  int ppt=0;
  int i=marker/6;
  int j=marker%6;
  for (int k=0;k<36;k++) if (ikey[k]==cs.PTspace.indexOf(flat.charAt(index))) { ppt=k; break; }
  
  int r=ppt/6;
  int c=ppt%6;
  int x=(r+ikey[marker]/6)%6;
  int y=(c+ikey[marker])%6;
  int ict=ikey[x*6+y];

  sb.append(cs.PTspace.charAt(ict));

  int start=r*6;
  int tmp=ikey[start+5];
  for (int k=5;k>0;k--) ikey[start+k]=ikey[start+k-1];
  ikey[start]=tmp;
  
  if (x==r)  y=(y+1)%6; // CT tile col changes
  if (i==r)  j=(j+1)%6; // marker col changes

  start=y;   // %6 already mandated;
  tmp=ikey[start+30];
  for (int k=5;k>0;k--) ikey[start+k*6]=ikey[start+k*6-6];
  ikey[start]=tmp;
  if (j==(y%6)) i=(i+1)%6; // marker row changes

  i=(i+ict/6)%6;
  j=(j+ict%6)%6;

  marker=i*6+j;
}
return sb.toString();
}
// ----------------------------------------------------------------------
public String applyNonce(String nonce) { return encode(nonce); }
// Advances internal state based on nonce (and returns encoded nonce, usually discarded)
// ----------------------------------------------------------------------
@Override
public String decode(String CT)
{
String flat=cs.flattenToCT(CT);

StringBuilder sb=new StringBuilder(CT.length());
for (int index=0;index<flat.length();index++) {
  int pct=0;
  int i=marker/6;
  int j=marker%6;
  int ict=cs.PTspace.indexOf(flat.charAt(index));
  for (int k=0;k<36;k++) if (ikey[k]==ict) { pct=k; break; }
  int x=pct/6;
  int y=pct%6;
  int r=(66+x-ikey[marker]/6)%6; // 66 to ensure +ve
  int c=(66+y-ikey[marker]%6)%6;

  int ipt=ikey[r*6+c];

  sb.append(cs.PTspace.charAt(ipt));
  int start=r*6;
  int tmp=ikey[start+5];
  for (int k=5;k>0;k--) ikey[start+k]=ikey[start+k-1];
  ikey[start]=tmp;
  if (x==r)  y=(y+1)%6; // CT tile col changes
  if (i==r)  j=(j+1)%6; // marker col changes

  start=y;
  tmp=ikey[start+30];
  for (int k=5;k>0;k--) ikey[start+k*6]=ikey[start+k*6-6];
  ikey[start]=tmp;
  if (j==(y%6)) i=(i+1)%6; // marker row changes

  i=(i+ict/6)%6;
  j=(j+ict)%6;

  marker=i*6+j;
}
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args) {

// Test vectors from Kaminsky paper  https://eprint.iacr.org/2017/339.pdf
// ................................................................
boolean pass=true;

ElsieFour ef;

String nonce="solwbf";
String target="i2zqpilr2yqgptltrzx2_9fzlmbo3y8_9pyssx8nf2";
String itwas ="im_about_to_put_the_hammer_down#rubberduck";
ef=new ElsieFour("xv7ydq#opaj_39rzut8b45wcsgehmiknf26l");
ef.applyNonce(nonce);  // Advance by nonce

String result=ef.encode(itwas);
pass&=result.equals(target);

ef=new ElsieFour("xv7ydq#opaj_39rzut8b45wcsgehmiknf26l");
ef.applyNonce(nonce);  // Advance by nonce
result=ef.decode(target);
pass&=result.equals(itwas);

ef=new ElsieFour("hxo_tp#a3jdnmq2glf75kw469eyrzbvci8us");
nonce="9kqhuo";
ef.applyNonce(nonce);
result=ef.encode("its_my_fathers_son_but_not_my_brother#its_me");
target="sdzyj54mwaibwzr9gd_79ogy789357fqv5ks_o2pxyqs";
pass&=result.equals(target);

ef=new ElsieFour("hxo_tp#a3jdnmq2glf75kw469eyrzbvci8us");
ef.applyNonce(nonce);  // Advance by nonce
result=ef.decode("sdzyj54mwaibwzr9gd_79ogy789357fqv5ks_o2pxyqs");
target="its_my_fathers_son_but_not_my_brother#its_me";
pass&=result.equals(target);

System.out.println(pass?"PASS":"***** FAILED **********");
}
}
