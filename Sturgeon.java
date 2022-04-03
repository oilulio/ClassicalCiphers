import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Sturgeon extends ClassicalCipher
{
// *** Compile with an encoding such as ISO-8859-1
// i.e. "javac -encoding ISO-8859-1 Sturgeon.java"
// This is to cope with the "£" sign in the text 
  
// Base class to conduct Sturgeon encryption and decryption.
// i.e. a Siemens and Halske T52
// see https://en.wikipedia.org/w/index.php?title=Siemens_and_Halske_T52&oldid=887489719

// Literature on web is unclear on LSb/MSb ordering.  In general the historical
// view seems to be ordered on LSb ... MSb, i.e. the opposite of stndard binary representation 

// Individual machines are T52A, T52D etc

// 'x' = 'pulse'  = binary 1
// '.' = no pulse = binary 0

/*	
Copyright (C) 2020-22  S Combes

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

public static final String BPsturgeon="/E4A9SIU3DRJNFCKTZLWHYPQOBG+MXV8";
                                       
// The Bletchley Park (BP) symbols for Sturgeon, in binary order (0-31).
// '00000'=0='/' is invalid character
// N.B. No distinction made for figure shift or not
// + & 8 are also known as + & - (Move to figure shift/move to letter shift)
// These are the characters you read singly in a stream. Later analysis based
// on earlier shift characters is required to get the true message.

public static final String sturgeonLetters="_E"+(char)0x0D+"A SIU"+(char)0x0A+"DRJNFCKTZLWHYPQOBG+MXV8";
public static final String sturgeonFigures="_3 - '87 ?4x,!:(5+)2£6019?& ./; ";
public static final String sturgeonChars;  // Characters Sturgeon can encode

static { sturgeonChars=Codespace.unique(sturgeonLetters+sturgeonFigures); }

public static final String[] PIN_PATTERNS = { // From MTC3 code
// 1234567890123456789012345678901234567890123456789012345678901234567890123
  ".xx.xxx...x..xx..x..xxxx..xxx.x.x.xxx..xx..xxx.xx.....xxx...xxxx.x......x", // A73
  ".xxxx..xxxxxx...x...x.x..xx.xxxxx.....xxx...xx.xxx.....xx.x.x.xxx.....x",   // B71
  ".xxxxx.x.xx..x.xx....xx.x....xxxx.xxx.xxx...x..x..xxx....xx....xx...x",     // C69
  ".xx.x.x...xxx.....xx.x....x..xxx..xxx..x.xxx...xx..x..x.xxxx..x...x",       // D67
  ".x...x...x.xxxxxxx.x.xxxxxx..x.x..xx.x....x.xxxxx..xxx.x.x.xx...x",         // E65
  ".x.x.xxxxx....x.xxxx..x.xx.xxxx.xx..xxx....x....xxx.xxxxxx...x.x",          // F64
  ".xxxx...xx..xx...x.xxxxx.x..x..xxx.x.xx.x.xx.x....xx.x..x.xxx",             // G61
  ".x..xxxxx...xxxx.x..xxxx...xx.x...xx.x.xx...xxxxxx..xx.x.xx",               // H59
  ".x.xx.xx..xx.x.xx...xxx...xxx..x.xxx.x....xxxx...x.xx",                     // J53 (no I)
  ".x....xxxxx...x..x.xxx..x.x.xxx..xxx.xxxx...xxx"                            // K47
};
public static String [] rollover;
static {
  rollover=new String[10];
  for (int i=0;i<10;i++) rollover[i]=PIN_PATTERNS[i]+PIN_PATTERNS[i].substring(0,13);
}
// The offset used by the irregular wheel stepping (T52d/e)
final static int [] stepOffset={25,24,23,23,22,22,20,20,18,16};

protected int [] MKU;           // Message Key Unit perm
protected int [] WSU;           // Wheel Setting Unit perm
protected int [] MKU_WSU;       // Wheel Setting Unit and Message Key Unit combined perm
protected Scrambler scrambler;  // Implements XOR and perm
protected Wheel [] wheels=new Wheel[10];

static int [] SRmask={0x200,0x100,0x80,0x40,0x20,0x10,0x08,0x04,0x02,0x01};  // Null mapping, gets replaced in subclass
// SR05..01..SR10..SR06 (later <<=1 reverses order)

static final char SHIFT_UP  =(char)27;
static final char SHIFT_DOWN=(char)31;
static final String [] ROMAN={"I","II","III","IV","V"};
static final String [] LATIN={"1","3","5","7","9"};
static final String [] LABELS=Stream.concat(Arrays.stream(ROMAN),Arrays.stream(LATIN))
                      .toArray(String[]::new);
// This reflects the bit order in the key, i.e. LABELS[0] is key bit 0 ("I")

static final int [][] FIXED_PAIRS={{1,2},{3,4},{5,6},{7,8},{9,10}}; // The fixed permutation
// ----------------------------------------------------------------------
Sturgeon(String wheelPos) 
{ 
super(new Codespace(sturgeonChars,sturgeonChars,BPsturgeon,BPsturgeon));

String [] pos=wheelPos.split(":");
if (pos.length!=10) throw new IllegalArgumentException("Wrong number of wheel start positions.  Found "+pos.length);
for (int i=0;i<10;i++) wheels[i]=new Wheel(Integer.parseInt(pos[i])-1,PIN_PATTERNS[i],stepOffset[i]);  // -1 because 0 base internally
}
// ----------------------------------------------------------------------
Sturgeon(int [] wheelPos) // Directly supplied numbers
{ 
super(new Codespace(sturgeonChars,sturgeonChars,BPsturgeon,BPsturgeon));
for (int i=0;i<10;i++) wheels[i]=new Wheel(wheelPos[i],PIN_PATTERNS[i],stepOffset[i]);  // 0 based data
}
// ----------------------------------------------------------------------
protected int [] getMessageKeyUnit(String mkuKey) { 
// Default is null mapping.  Only T52c and T52ca use other.	
// Same effective function as WheelSettingUnit (perm of 10 inputs) but
// is changed message-by-message not day-by-day. 
int [] mapping=new int[10];
for (int i=0;i<10;i++) mapping[i]=i; // Null
return mapping;
}
// ----------------------------------------------------------------------
protected int SRlogic(int input) { 
// Only T52c and T52ca and T52e use real logic.	But ab/d can use null setting.
// Destroys 1-1 mapping between Keywheels and inputs to XOR or Perm
// Value of SRmask different in children
int output=0;
for (int i=0;i<10;i++) {
  output<<=1;
  output|=(Integer.bitCount(input&SRmask[i])&1);
}
return output;
}
// ----------------------------------------------------------
protected boolean [] toAdvance(int z) { // Default, gets overridden in T52d,e
boolean [] ALL_ADVANCE={true,true,true,true,true,true,true,true,true,true};
return ALL_ADVANCE;
}
// ----------------------------------------------------------------------
protected String wheelData() {
int [] inv=new int[10];
for (int i=0;i<10;i++) inv[MKU_WSU[i]]=i;

StringBuilder sb=new StringBuilder(nL+"---------- Wheels ---------"+nL);
for (int i=0;i<10;i++)  sb.append(String.format("%-3s ",LABELS[i])+wheels[inv[i]].toString());

return sb.toString();
}
// ----------------------------------------------------------------------
public String toString() 
{ 
int [] inv=new int[10];
for (int i=0;i<10;i++) inv[MKU_WSU[i]]=i;

StringBuilder sb=new StringBuilder(nL+"---------- Wheels ---------"+nL);
for (int i=0;i<10;i++)  sb.append(String.format("%-3s ",LABELS[i])+wheels[inv[i]].toString());

return (this.getClass().getName()+" Cipher :  "+nL+wheelData()+" "+nL);} // Do not print codespace
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{
StringBuilder sb=new StringBuilder(PT.length());
boolean shift=false;

for (int i=0;i<PT.length();i++) {
  int key=0;
  for (int physicalWheel=0;physicalWheel<10;physicalWheel++) {
	int mappedWheel=MKU_WSU[physicalWheel];
	
	if (wheels[physicalWheel].getSense()) key|=(1<<mappedWheel);
  }
  boolean [] advance=toAdvance((i==0)?0:BPsturgeon.indexOf(PT.charAt(i-1)));
  
  for (int physicalWheel=0;physicalWheel<10;physicalWheel++) {
	if (advance[physicalWheel]) wheels[physicalWheel].advance();
  }
  key=SRlogic(key); // Sometimes null function, e.g. for T52ab.
  int input=BPsturgeon.indexOf(PT.charAt(i));
  if (input<0) throw new IllegalArgumentException("Invalid character in PT");

  sb.append(BPsturgeon.charAt(scrambler.getEncrypt(key,input)));
}
return sb.toString();
}
// ----------------------------------------------------------
@Override
public String decode(String CT)
{
StringBuilder sb=new StringBuilder(CT.length());
boolean shift=false;
int PT=0;
for (int i=0;i<CT.length();i++) {
  int key=0;
  for (int physicalWheel=0;physicalWheel<10;physicalWheel++) {
	int mappedWheel=MKU_WSU[physicalWheel];
	if (wheels[physicalWheel].getSense()) key|=(1<<mappedWheel);
  }
  boolean [] advance=toAdvance(PT);
  for (int physicalWheel=0;physicalWheel<10;physicalWheel++) {
	if (advance[physicalWheel]) wheels[physicalWheel].advance();
  }  
  key=SRlogic(key); // Sometimes null function, e.g. for T52ab.
  int input=BPsturgeon.indexOf(CT.charAt(i));
  if (input<0) throw new IllegalArgumentException("Invalid character in CT");
  PT=scrambler.getDecrypt(key,input);
  sb.append(BPsturgeon.charAt(PT));
}	
return sb.toString();
}
// ----------------------------------------------------------------------
String insertShifts(String PT) 
{
StringBuilder sb=new StringBuilder();
boolean shift=false;

// Convert into 5 bit chars, insert Letter/Figure shifts where required
for (int i=0;i<PT.length();i++) {
  if (cs.PTspace.indexOf(PT.charAt(i))==(-1)) throw new
    IllegalArgumentException(
          "Not a character Sturgeon can encode "+PT.substring(i,i+1));

  boolean needsShift;

  // We know it's a valid character, so must be in Figures or Letters
  // Determine if it is the opposite to current shift setting  
  if (shift) needsShift=sturgeonFigures.indexOf(PT.charAt(i))==(-1);
  else       needsShift=sturgeonLetters.indexOf(PT.charAt(i))==(-1);

  if (needsShift) { // Insert the shift character
    sb.append(shift?SHIFT_UP:SHIFT_DOWN);
    shift=!shift;
  }
  sb.append((char)(shift?sturgeonFigures.indexOf(PT.charAt(i))
                        :sturgeonLetters.indexOf(PT.charAt(i))));
}
return sb.toString();
}
// ----------------------------------------------------------------------
protected class Wheel
{
int position;
int offset;     // The offset used by the irregular wheel stepping (T52d/e)
String pattern;
// ---------------------------------------------------------------------
Wheel(int position,String pattern,int offset)
{
// A pin pattern.  On movement, move to next bit more significant than me
this.position=position%pattern.length();
if (position>=pattern.length()) System.out.println("Wheel position wrapped into bounds");
//if (position>=pattern.length()) throw new IllegalArgumentException("Wheel position out of bounds");
this.pattern=pattern;
this.offset=offset;
}
// ---------------------------------------------------------------------
boolean getSense() { return (pattern.charAt(position)=='x'); }
// ---------------------------------------------------------------------
boolean getOffsetSense() { 
    return (pattern.charAt((position+offset)%pattern.length())=='x'); }
// ---------------------------------------------------------------------
void advance()     { position=(position+1)%pattern.length(); }
// ---------------------------------------------------------------------
public String toString()
{
return "Wheel of length "+String.valueOf(pattern.length())+
                                  " "+pattern+" @ position "+(position+1)+nL;
}
}
// ---------------------------------------------------------------------
protected class Scrambler 
{ 	
// Implements the mixed XOR and permutation scrambler for Sturgeon machines
// Uses numbering as follows (See Fig 8 of Sturgeon, The FISH BP Never Really Caught
// Frode Weierud)  But note LSb and MSb hard to disentangle, so used match to MTC3 code
// Bit 0 is LSb in table below.

//  Note that pairs[][] does not contain zero-based numbers (uses 1-10)

//   Bit in       Plug           Plug           Bit in
// input data                                 output data + 'OP'
//     0  --->---(1  1)---->----(10 10)---->---- 100
//     1  --->---(8  8)---->----(9   9)---->---- 101
//     2  --->---(6  6)---->----(7   7)---->---- 102
//     3  --->---(4  4)---->----(5   5)---->---- 103
//     4  --->---(2  2)---->----(3   3)---->---- 104
//    inputBitToPlug    advance         advance

protected int [][] pairs;

private final int [] inputBitToPlug={1,8,6,4,2};
private final int [] outputBitToPlug={10,9,7,5,3};

private final int OP=100;
private int [] advance={0,10,3,OP+4,5,OP+3,7,OP+2,9,OP+1,OP+0}; // Data starts at index 1
private int [] retreat={0,OP+0,OP+4,2,OP+3,4,OP+2,6,OP+1,8,1};  // Data starts at index 1
// Next plug to advance to after the last.  
// e.g. plug 1 connects onwards to plug 10 "1)---->----(10" in diagram.
// Numbers >=OP dictate an output bit.  Bit derived by subtracting OP.
// ---------------------------------------------------------------------
Scrambler (int [][] pairs) 
{
// Defensive copy option 
this.pairs=new int[5][2];
for (int i=0;i<5;i++)
  for (int j=0;j<2;j++)
	this.pairs[i][j]=pairs[i][j];
} 
// ---------------------------------------------------------------------
protected int getEncrypt(int key,int character) 
{	
int output=0;              // All bits zero
int input=(character^key); // XOR with key's lowest 5 bits (can ignore highest)

for (int inBit=0;inBit<5;inBit++) {
  if ((input&(1<<inBit))!=0) { // A set-bit to propagate
    int thisPlug=inputBitToPlug[inBit];
    int depth=0;
    do {
      for (int pr=0;pr<5;pr++) {
    	if ((key&(1<<(5+pr)))==0) { // Perms are active low. +5 as perms are high bits
          if (pairs[pr][0]==thisPlug) { thisPlug=pairs[pr][1]; break; }
          if (pairs[pr][1]==thisPlug) { thisPlug=pairs[pr][0]; break; }
        }
      }
      thisPlug=advance[thisPlug];
	    if (thisPlug>=OP) { output|=(1<<(thisPlug-OP)); break; }
	  } while (++depth<8);
	  if (depth>=8) { throw new IllegalArgumentException("Invalid plugs in configurable permutation"); }
    }
}
return output;
}
// ---------------------------------------------------------------------
protected int getDecrypt(int key,int character) 
{
int output=0;         // All bits zero

for (int inBit=0;inBit<5;inBit++) {
  if ((character&(1<<inBit))!=0) { // A set-bit to propagate
    int thisPlug=outputBitToPlug[inBit];
    int depth=0;
    do {
      for (int pr=0;pr<5;pr++) {
    	if ((key&(1<<(5+pr)))==0) { // Perms are active low. +5 as perms are high bits
          if (pairs[pr][0]==thisPlug) { thisPlug=pairs[pr][1]; break; }
          if (pairs[pr][1]==thisPlug) { thisPlug=pairs[pr][0]; break; }
        }
      }
      thisPlug=retreat[thisPlug];
	  
	    if (thisPlug>=OP) { output|=(1<<(thisPlug-OP)); break; }
	  } while (++depth<8);
	  if (depth>=8) { throw new IllegalArgumentException("Invalid plugs in configurable permutation"); }
    }
}
return (output^key)&0x1F; // XOR with key's lowest 5 bits 
}
}
}