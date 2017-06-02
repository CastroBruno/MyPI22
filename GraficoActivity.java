package br.com.gg.mypi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraficoActivity extends AppCompatActivity {

    private int foreignKey = LoginActivity.FKey;
    private int botao = 1;
    LineChart lineChart;

    private class LongOperation extends AsyncTask<Object, Object, ArrayList<Entry>> {

        @Override
        protected ArrayList<Entry> doInBackground(Object... params) {

            Connection db2 = null;
            ArrayList<Entry> y = null;
            ResultSet lg2;
            Statement st2;
            try {
                //Thread.sleep(500);
                Class.forName("org.postgresql.Driver");
                db2 = DriverManager.getConnection("jdbc:postgresql://ec2-184-73-199-72.compute-1.amazonaws.com:5432/d9krqs4b40hebl?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory", "ipsjzpheswtzlh", "a5f4879460047281d282829f6e0b6fa4f0771722744aafaf627a4da8279127a8");
                st2 = db2.createStatement();
                //lg2 = st2.executeQuery("SELECT idd,dados,id_fk,datar,horar FROM tbdados Where id_fk ="+foreignKey);
                lg2 = st2.executeQuery("SELECT dados FROM tbdados Where id_fk ="+foreignKey);

                y = new ArrayList<>();
                Array[] dados = new Array[30];
                //int[] idd = new int[30];
                //Date[] datar = new Date[30];
                //Time[] horar = new Time[30];
                int i=0;
                while (lg2.next()) {
                    //idd[i] = lg2.getInt("idd");
                    //horar[i] = lg2.getTime("horar");
                    //datar[i] = lg2.getDate("datar");
                    dados[i] = lg2.getArray("dados");
                    i++;
                }
                String data = Arrays.toString(dados);
                //System.out.println(data);
                Pattern pattern = Pattern.compile("\\{(.*?)\\}");
                Matcher matcher = pattern.matcher(data);
                int j =0;
                String[] parts;
                Float[][] Valores = new Float[30][30];
                    while (matcher.find())
                    {
                        //System.out.println(matcher.group(1));
                        parts=matcher.group(1).split(",");
                        for (int k = 0; k < 15 ; k++) {
                            if(parts[k].toString() != null) {
                                Valores[j][k] = Float.valueOf(parts[k]);
                            }
                        }
                        j++;
                    }
                for (int k = 0; k < 15 ; k++) {
                    if(Valores[botao][k] != null) {
                        y.add(new Entry(k, Valores[botao][k]));
                    }
                    //System.out.println(Valores[1][k]);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                finish();
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
        lineChart = (LineChart)findViewById(R.id.lineChart);
        lineChart.setDescription(null);
        //lineChart.setDescription(null);
        new LongOperation().execute("");
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();

        Button botao1 = (Button)findViewById(R.id.button2);
        botao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(botao < 5)
                {

                    botao = botao+1;
                    new LongOperation().execute("");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getBaseContext(),"Próximo",Toast.LENGTH_LONG).show();
                    lineChart.notifyDataSetChanged(); // let the chart know it's data changed
                    lineChart.invalidate();
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Fim",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
