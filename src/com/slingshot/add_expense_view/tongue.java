package com.slingshot.add_expense_view;

import android.content.res.Configuration;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.slingshot.R;
import com.slingshot.lib.saveFile;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: brodjag
 * Date: 14.02.13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class tongue {
    addExpensActivity con;
    public tongue(addExpensActivity c){
           con=c;

    con.findViewById(R.id.tongue_perent).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            return;
        }
    });
    con.findViewById(R.id.tongue_perent_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
 //   InitHideTongue();
        InitShowTongue();
        addItems();
        addItemsHr();
    }

    private void InitHideTongue() {
        //con.findViewById(R.id.tongue_hide).setOnClickListener();
    }

    private void InitShowTongue() {
        con.findViewById(R.id.show_tongue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}

                int orientation = con.getResources().getConfiguration().orientation;


                Animation showTongue = AnimationUtils.loadAnimation(con, R.anim.tongue_show);
                showTongue.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                if (orientation== Configuration.ORIENTATION_PORTRAIT){
                    con.findViewById(R.id.tongue_perent).setVisibility(View.VISIBLE);
                    LinearLayout tongue=(LinearLayout) con.findViewById(R.id.tongue);
                    tongue.setAnimation(showTongue);
                }else {
                    con.findViewById(R.id.tongue_perent_hr).setVisibility(View.VISIBLE);
                    LinearLayout tongue=(LinearLayout) con.findViewById(R.id.tongue_hr);
                    tongue.setAnimation(showTongue);
                }


            }
        });
    }


private void addItems(){
    saveFile sf=new saveFile(con);
    Element el= sf.readXmlFile("ExpenseCodes.xml");
    Element body=(Element) el.getElementsByTagName("soap:Body").item(0);
    Element GetExpenseCodesResponse=(Element)  (body).getElementsByTagName("GetExpenseCodesResponse").item(0);
    Element GetExpenseCodesResult=(Element) (GetExpenseCodesResponse).getElementsByTagName("GetExpenseCodesResult").item(0);
    String Code= GetExpenseCodesResult.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
    NodeList ExpenseCodes=((Element)GetExpenseCodesResult.getElementsByTagName("ExpenseCodes").item(0)).getElementsByTagName("string");
    LinearLayout tongueList=(LinearLayout) con.findViewById(R.id.tongue_list);

    for(int i=0; i<ExpenseCodes.getLength(); i=i+4){
       View contener= con.getLayoutInflater().inflate(R.layout.tongue_expense_code_contaner,null);

        for (int n=i;n<(i+4); n++){

           if(n<ExpenseCodes.getLength()){
                View item=con.getLayoutInflater().inflate(R.layout.expense_code_item,null);
                final String expenseCode=ExpenseCodes.item(n).getFirstChild().getNodeValue().toString();
                ((TextView) item.findViewById(R.id.item_name)).setText(expenseCode);



               item.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                       con.ExpenseCode=expenseCode;

                       ((TextView) con.findViewById(R.id.selected_expense_code_name)).setText(expenseCode);

                       Animation hideTongue = AnimationUtils.loadAnimation(con, R.anim.tongue_hide);

                       hideTongue.setAnimationListener(new Animation.AnimationListener() {
                           @Override
                           public void onAnimationStart(Animation animation) { }

                           @Override
                           public void onAnimationEnd(Animation animation) {   con.findViewById(R.id.tongue_perent).setVisibility(View.GONE); }

                           @Override
                           public void onAnimationRepeat(Animation animation) {}
                       });

                       LinearLayout tongue=(LinearLayout) con.findViewById(R.id.tongue);
                       tongue.setAnimation(hideTongue);


                   }
               });//end onClick listener

                (( LinearLayout) contener.findViewById(R.id.main_contaner)).addView(item);
            }else {

            }


        }
        //spinerItems.add(ExpenseCodes.item(i).getFirstChild().getNodeValue().toString());





       tongueList.addView(contener);
    }


}



//add item HORISONTAL
private void addItemsHr(){
    saveFile sf=new saveFile(con);
    Element el= sf.readXmlFile("ExpenseCodes.xml");
    Element body=(Element) el.getElementsByTagName("soap:Body").item(0);
    Element GetExpenseCodesResponse=(Element)  (body).getElementsByTagName("GetExpenseCodesResponse").item(0);
    Element GetExpenseCodesResult=(Element) (GetExpenseCodesResponse).getElementsByTagName("GetExpenseCodesResult").item(0);
    String Code= GetExpenseCodesResult.getElementsByTagName("Code").item(0).getFirstChild().getNodeValue().toString();
    NodeList ExpenseCodes=((Element)GetExpenseCodesResult.getElementsByTagName("ExpenseCodes").item(0)).getElementsByTagName("string");
    LinearLayout tongueList=(LinearLayout) con.findViewById(R.id.tongue_list_hr);

    for(int i=0; i<ExpenseCodes.getLength(); i=i+6){
        View contener= con.getLayoutInflater().inflate(R.layout.tongue_expense_code_contaner,null);

        for (int n=i;n<(i+6); n++){

            if(n<ExpenseCodes.getLength()){
                View item=con.getLayoutInflater().inflate(R.layout.expense_code_item,null);
                final String expenseCode=ExpenseCodes.item(n).getFirstChild().getNodeValue().toString();
                ((TextView) item.findViewById(R.id.item_name)).setText(expenseCode);



                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {     ((Vibrator) con.getSystemService(con.VIBRATOR_SERVICE)).vibrate(50); } catch (Exception e) {}
                        con.ExpenseCode=expenseCode;

                        ((TextView) con.findViewById(R.id.selected_expense_code_name)).setText(expenseCode);

                        Animation hideTongue = AnimationUtils.loadAnimation(con, R.anim.tongue_hide);

                        hideTongue.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) { }

                            @Override
                            public void onAnimationEnd(Animation animation) {   con.findViewById(R.id.tongue_perent_hr).setVisibility(View.GONE); }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });

                        LinearLayout tongue=(LinearLayout) con.findViewById(R.id.tongue_hr);
                        tongue.setAnimation(hideTongue);


                    }
                });//end onClick listener

                (( LinearLayout) contener.findViewById(R.id.main_contaner)).addView(item);
            }else {

            }


        }






        tongueList.addView(contener);
    }


}


}
