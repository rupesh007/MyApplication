package layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anonymous_pal.myapplication.R;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.OnDataPointTapListener;

import java.util.Random;

/**
 * Created by anonymous_pal on 21.4.2017.
 */

public class fragment_1 extends Fragment {


    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
  //  private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;

    // private double graph2LastXValue = 5d;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_1, container, false);

        GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
       // GraphView graph2 = (GraphView) rootView.findViewById(R.id.graph2);
        graph.setTitle("ParkinsonTremorNotifier");

        mSeries1 = new LineGraphSeries<>(generateData());
        //mSeries1.setBackgroundColor(0x56f44);
        mSeries1.setTitle("Xout Values");
        mSeries1.setColor(Color.GREEN);
        graph.addSeries(mSeries1);


        mSeries2 = new LineGraphSeries<>(generateData());
       // mSeries2.setBackgroundColor(0x424ef4);
        mSeries2.setTitle("Yout values");
        mSeries2.setColor(Color.BLUE);
        graph.addSeries(mSeries2);

        mSeries3 = new LineGraphSeries<>(generateData());
       // mSeries3.setBackgroundColor(0xf44253);
        mSeries3.setTitle("Zout values");
        mSeries2.setColor(Color.RED);
        graph.addSeries(mSeries3);

        graph.getViewport().setScrollable(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }

    }




   /* @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MainActivity.ARG_SECTION_NUMBER));
    }*/

   @Override
    public void onResume() {
       super.onResume();


        mTimer1 = (new Runnable() {
            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);

                mSeries2.resetData(generateData());
                mHandler.postDelayed(this, 300);

                mSeries3.resetData(generateData());
                mHandler.postDelayed(this, 300);

            }
        });

    /*    parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        });   */

       mHandler.postDelayed(mTimer1, 1000);

    /* mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                mHandler.postDelayed(this, 200);
            }
        };

     /*   parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries1.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                mHandler.postDelayed(this, 200);
            }
        });  */

     //  mHandler.postDelayed(mTimer2, 1000);
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        //mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }


   private DataPoint[] generateData() {
       int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x= i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }


    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }



}
