package com.example.anonymous_pal.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import java.util.Calendar;
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
        graph.setTitle("ParkinsonTremorNotifier");

        mSeries1 = new LineGraphSeries<>(generateData());
        mSeries1.setTitle("Xout");
        mSeries1.setColor(Color.GREEN);
        graph.addSeries(mSeries1);

        mSeries2 = new LineGraphSeries<>(generateData());
        mSeries2.setTitle("Yout");
        mSeries2.setColor(Color.BLUE);
        graph.addSeries(mSeries2);

        mSeries3 = new LineGraphSeries<>(generateData());
        mSeries3.setTitle("Zout");
        mSeries2.setColor(Color.RED);
        graph.addSeries(mSeries3);

        graph.getViewport().setScrollable(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setMargin(100);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Accelerometer Reading");

        // set manual x bounds to have nice steps
       // graph.getViewport().setMinX(d1.getTime());
       // graph.getViewport().setMaxX(d1.getTime());
      //  graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);


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

/*    @Override
    public void onResume() {
        super.onResume();
    }  */

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTimer1 = (new Runnable() {

            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mSeries2.resetData(generateData());
                mSeries3.resetData(generateData());
                mHandler.postDelayed(this, 300);

            }
        });

        mHandler.postDelayed(mTimer1, 300);


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
                  mSeries2.resetData(generateData());
                  mSeries3.resetData(generateData());
                  mHandler.postDelayed(this, 300);

              }
          });

       mHandler.postDelayed(mTimer1, 300);

    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        //mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }


   private DataPoint[] generateData() {
        int count = 30;
        Calendar calendar = Calendar.getInstance();

        DataPoint[] values = new DataPoint[count];

        for (int i=0; i<count; i++) {
           // double d1=calendar.getTimeInMillis();
            double d1= i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(d1, y);
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
