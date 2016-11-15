package cpe.spu.locatekid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowAdapter extends BaseAdapter {

    //ประกาศตัวแปร
    private Context context;
    private String[] nameStrings, surStrings, classString, picStrings;

    public ShowAdapter(Context context,
                       String[] nameStrings,
                       String[] surStrings,
                       String[] classString,
                       String[] picStrings) {
        this.context = context;
        this.nameStrings = nameStrings;
        this.surStrings = surStrings;
        this.classString = classString;
        this.picStrings = picStrings;
    }

    @Override
    public int getCount() {
        return nameStrings.length;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = layoutInflater.inflate(R.layout.activity_show_adapter, viewGroup, false);

        TextView idStuTextView = (TextView) view1.findViewById(R.id.textView17);
        idStuTextView.setText(nameStrings[i]);

        TextView nameTextView = (TextView) view1.findViewById(R.id.textView18);
        nameTextView.setText(surStrings[i]);

        TextView classTextView = (TextView) view1.findViewById(R.id.textView19);
        classTextView.setText(classString[i]);

        ImageView imageView = (ImageView) view1.findViewById(R.id.imageView5);
        Picasso.with(context).load(picStrings[i]).resize(80, 100).into(imageView);

        return view1;
    }
} // Main Class
