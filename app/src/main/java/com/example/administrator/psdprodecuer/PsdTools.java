package com.example.administrator.psdprodecuer;

/**
 * Created by Administrator on 2016/5/10 0010.
 */
public class PsdTools {
    private static String [] moPSDs = {"022222","012222","011222","011122","011112","011111","021111","022111","022211","022221",//0~9
            "112000","121110","121210","121100","110000","111210","122100","111110","111000","112220","121200","112110","122000", //a~z
            "121000","122200","112210","122120","112100","111100","120000","111200","111120","112200","121120","121220","122110",
            "212000","221110","221210","221100","210000","211210","222100","211110","211000","212220","221200","212110","222000",//A~Z
            "221000","222200","212210","222120","212100","211100","220000","211200","211120","212200","221120","221220","222110"};
    private static int nummoPSD = 62;
    public static char getChar(String moPSD){
        for(int i= 0;i<nummoPSD;i++){
            if(moPSD.equals(moPSDs[i])){
                if(i<=9){
                    return (char)(i+48);
                }else if(9<i&&i<=35){
                    return (char)(i+87);
                }else{
                    return (char)(i+29);
                }
            }
        }
        return (char)(33);//表示不符合密码，返回的是！
    }

    public static int [] getMoPSD(String value){
        int[] moPSD = new int[6];
        moPSD[0] = 4;
        char val;
        if(value.equals("")){
            return moPSD;
        }else{
            val= value.charAt(0);
            if(val>='0'&&val<='9'){
                int va = val-48;
                for(int i =0;i<6;i++){
                    moPSD[i]=Integer.parseInt(moPSDs[va].charAt(i)+"");
                }
            }else if (val>='a'&&val<='z'){
                int va = val-87;
                for(int i =0;i<6;i++) {
                    moPSD[i] = Integer.parseInt(moPSDs[va].charAt(i) + "");
                }
            }else if(val>='A'&&val<='Z'){
                int va = val-29;
                for(int i =0;i<6;i++) {
                    moPSD[i] = Integer.parseInt(moPSDs[va].charAt(i) + "");
                }
            }
            return moPSD;
        }

    }

}
