package utils;




import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Apply specified font for all text views (including nested ones) in the specified root view.
 */

public class ApplyFont {


    private static final String TAG = ApplyFont.class.getSimpleName();
    public final static String FONT_TYPEFACE = "Roboto_Regular.ttf";
    public final static String FONT_SEMIBOLDTYPEFACE = "Roboto_Medium.ttf";
    public final static String FONT_TYPEFACE_Bold = "Roboto_Bold.ttf";
    public final static String FONT_TYPEFACE_LIGHT = "Roboto_Light.ttf";
    public static Typeface tf;
    public static Typeface button;
    public static Typeface boldFont;

    public static void applyFont(final Context context, final View root) {
        try {
            if(tf==null)
                tf = Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView || root instanceof EditText)
                ((TextView) root).setTypeface(tf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyLightFont(final Context context, final View root) {
        try {
            if(tf==null)
                tf = Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE_LIGHT);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView || root instanceof EditText)
                ((TextView) root).setTypeface(tf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyMediumFont(final Context context, final View root) {
        try {
            if(button==null)
                button = Typeface.createFromAsset(context.getAssets(), FONT_SEMIBOLDTYPEFACE);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyMediumFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView || root instanceof EditText)
                ((TextView) root).setTypeface(button);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void applyBold(final Context context, final View root) {
        try {
            if(boldFont==null)
                boldFont = Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE_Bold);
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                int childCount = viewGroup.getChildCount();
                for (int i = 0; i < childCount; i++)
                    applyFont(context, viewGroup.getChildAt(i));
            } else if (root instanceof TextView || root instanceof EditText)
                ((TextView) root).setTypeface(boldFont);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MySpinnerAdapter extends ArrayAdapter<String> {

        Context context;

        public MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
            this.context = context;
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ApplyFont.applyFont(context, view);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            ApplyFont.applyFont(context, view);
            return view;
        }
    }

    public static void overrideFonts(Context context, View v) {
        ViewGroup picker;
        try {
            picker = (DatePicker) v;
        } catch (Exception e) {
            picker = (TimePicker) v;
        }
        ViewGroup layout1 = (ViewGroup) picker.getChildAt(0);
        if (picker instanceof TimePicker) {
            if (layout1.getChildAt(1) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(Color.BLACK);
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
            if (layout1.getChildAt(2) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(Color.BLACK);
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
            if (layout1.getChildAt(0) instanceof NumberPicker) {
                NumberPicker v1 = (NumberPicker) layout1.getChildAt(1);
                final int count = v1.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = v1.getChildAt(i);

                    try {
                        Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                        wheelpaint_field.setAccessible(true);
                        ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        ((Paint) wheelpaint_field.get(v1)).setColor(Color.BLACK);
                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                        v1.invalidate();
                    } catch (Exception e) {
                        //TODO catch.
                        //If java cant find field then it will catch here and app wont crash.
                    }
                }
            }
        }
        ViewGroup layout=(ViewGroup)layout1.getChildAt(0);

        if(layout!=null)
            for (int j = 0; j < 3; j++) {
                try {
                    if (layout.getChildAt(j) instanceof NumberPicker) {
                        NumberPicker v1 = (NumberPicker) layout.getChildAt(j);
                        final int count = v1.getChildCount();
                        for (int i = 0; i < count; i++) {
                            View child = v1.getChildAt(i);

                            try {
                                Field wheelpaint_field = v1.getClass().getDeclaredField("mSelectorWheelPaint");
                                wheelpaint_field.setAccessible(true);
                                ((Paint) wheelpaint_field.get(v1)).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                                ((Paint) wheelpaint_field.get(v1)).setColor(Color.BLACK);
                                ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_TYPEFACE));
                                v1.invalidate();
                            } catch (Exception e) {
                                //TODO catch.
                                //If java cant find field then it will catch here and app wont crash.
                            }
                        }
                    }
                } catch (Exception e) {
                    //TODO catch.
                    //If java cant find field then it will catch here and app wont crash.
                }
            }

    }
    public static String convertfromArabic(String value) {
        String newValue = ((((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫",".")));
        newValue=newValue.replace("\",","*&^");
        newValue=newValue.replace(",",".");
        newValue=newValue.replace("*&^","\",");
        return newValue;
    }
}
