package br.com.gg.mypi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

public class GraficoActivity extends AppCompatActivity {

    private int foreignKey = LoginActivity.FKey;
    LineChart lineChart;

    private class LongOperation extends AsyncTask<Object, Object, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(Object... params) {

            Connection db2 = null;
            ArrayList<Entry> y = null;
            ResultSet lg2;
            Statement st2;
            try {
                Thread.sleep(500);
                Class.forName("org.postgresql.Driver");
                db2 = DriverManager.getConnection("jdbc:postgresql://ec2-184-73-199-72.compute-1.amazonaws.com:5432/d9krqs4b40hebl?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory", "ipsjzpheswtzlh", "a5f4879460047281d282829f6e0b6fa4f0771722744aafaf627a4da8279127a8");
                st2 = db2.createStatement();
                lg2 = st2.executeQuery("SELECT idd,dados,id_fk,datar,horar FROM tbdados Where id_fk ="+foreignKey);

                y = new ArrayList<>();
                Array[] dados = new Array[30];
                int[] idd = new int[30];
                Date[] datar = new Date[30];
                Time[] horar = new Time[30];
                while (lg2.next()) {
                    for (int i = 0; i < 30; i++) {
                        idd[i] = lg2.getInt("idd");
                        dados[i] = lg2.getArray("dados");
                        horar[i] = lg2.getTime("horar");
                        datar[i] = lg2.getDate("datar");
                    }
                }
                BigDecimal[] dadosI = new BigDecimal[30];
                for (int i = 0; i <30 ; i++) {
                    Object n = dados[i].getArray();
                    dadosI[i] = new BigDecimal(n.toString());
                }
                for (int i = 0; i < 30 ; i++) {
                    y.add(new Entry(i,dadosI[i].intValueExact()));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                db2.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return y;
        }
        protected void onPostExecute(final ArrayList<Entry> y){
                LineDataSet Ydataset = new LineDataSet(y,"Potencia");
                lineChart.setData(new LineData(Ydataset));
                //finish();
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        new LongOperation().execute("");
        ArrayList<Entry> t = new ArrayList<>();
        for (int i = 1; i < 12; i++) {
            t.add(new Entry(i, i));
        }
        LineDataSet Tdataset = new LineDataSet(t, "Potencia");
        //lineChart.setData(new LineData(Ydataset));
    }
}
