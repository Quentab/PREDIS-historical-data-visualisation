package ihm;

import com.sun.javafx.charts.ChartLayoutAnimator;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateAxis extends Axis<Date> {

    private static final int[] intervals = new int[]{
            Calendar.YEAR,
            Calendar.MONTH,
            Calendar.WEEK_OF_YEAR,
            Calendar.DATE,
            Calendar.HOUR,
            Calendar.MINUTE,
            Calendar.SECOND,
            Calendar.MILLISECOND
    };

    private Date minDate, maxDate;

    protected final LongProperty currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");
    protected final LongProperty currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");

    public DateAxis() {
    }

    public DateAxis(Date lowerBound, Date upperBound) {
        setAutoRanging(false);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    @Override
    protected Object autoRange(double length) {
        if (isAutoRanging()) {
            return new Object[]{minDate, maxDate};
        } else {
            if (lowerBound.get() == null || upperBound.get() == null) {
                throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
            }
            return getRange();
        }
    }

    @Override
    public void invalidateRange(List<Date> list) {
        super.invalidateRange(list);

        Collections.sort(list);
        if (list.size() == 0) {
            minDate = maxDate = new Date();
        } else if (list.size() == 1) {
            minDate = maxDate = list.get(0);
        } else if (list.size() > 1) {
            minDate = list.get(0);
            maxDate = list.get(list.size() - 1);
        }
    }

    private ChartLayoutAnimator animator = new ChartLayoutAnimator(this);
    private Object currentAnimationID;

    @Override
    protected void setRange(Object range, boolean animating) {
        Object[] r = (Object[]) range;
        Date oldLowerBound = getLowerBound();
        Date oldUpperBound = getUpperBound();
        setLowerBound((Date) r[0]);
        setUpperBound((Date) r[1]);

        if (animating) {
            final Timeline timeline = new Timeline();
            timeline.setAutoReverse(false);
            timeline.setCycleCount(1);
            final AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    requestAxisLayout();
                }
            };
            timer.start();

            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    timer.stop();
                    requestAxisLayout();
                }
            });
            KeyFrame kf = new KeyFrame(Duration.ZERO,

                    new KeyValue(currentLowerBound, oldLowerBound.getTime()),

                    new KeyValue(currentUpperBound, oldUpperBound.getTime())

            );
            KeyValue keyValue = new KeyValue(currentLowerBound, ((Date) r[0]).getTime());
            KeyValue keyValue2 = new KeyValue(currentUpperBound, ((Date) r[1]).getTime());

//            animator.stop(currentAnimationID);
//            currentAnimationID = animator.animate(new KeyFrame(Duration.seconds(3), keyValue, keyValue2));

            timeline.getKeyFrames().addAll(new KeyFrame(Duration.millis(3000), keyValue, keyValue2));
            timeline.play();
        } else {
            currentLowerBound.set(getLowerBound().getTime());
            currentUpperBound.set(getUpperBound().getTime());
        }
    }

    @Override
    protected Object getRange() {
        return new Object[]{getLowerBound(), getUpperBound()};
    }

    @Override
    public double getZeroPosition() {
        return 0;
    }

    @Override
    public double getDisplayPosition(Date date) {
        final double length = (Side.TOP.equals(getSide()) || Side.BOTTOM.equals(getSide())) ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        // Then get the difference from the actual date to the min date and divide it by the total difference.
        // We get a value between 0 and 1, if the date is within the min and max date.
        double d = (date.getTime() - currentLowerBound.get()) / diff;
        //System.out.println(d * range + getZeroPosition());
        // Multiply this percent value with the range and add the zero offset.

        return d * range + getZeroPosition();
    }

    @Override
    public Date getValueForDisplay(double v) {
        final double length = (Side.TOP.equals(getSide()) || Side.BOTTOM.equals(getSide())) ? getWidth() : getHeight();

        // Get the difference between the max and min date.
        double diff = currentUpperBound.get() - currentLowerBound.get();

        // Get the actual range of the visible area.
        // The minimal date should start at the zero position, that's why we subtract it.
        double range = length - getZeroPosition();

        // If 0 is the minDate and 1 is the maxDate this is the value between 0 and 1 representing the date.
        double factor = (v - getZeroPosition()) / range;

        // To get the actual date, multiply the factor with the difference and add the minDate.
        return new Date((long) (factor * diff + currentLowerBound.get()));
    }


    @Override
    public boolean isValueOnAxis(Date date) {
        return getDisplayPosition(date) > 0 && date.getTime() < currentUpperBound.get();
    }

    @Override
    public double toNumericValue(Date date) {
        return date.getTime();
    }

    @Override
    public Date toRealValue(double v) {
        return new Date((long) v);
    }

    @Override
    protected List<Date> calculateTickValues(double v, Object o) {

        List<Date> dateList = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();

        // The preferred gap which should be between two tick marks.
        double averageTickGap = 100;
        double averageTicks = v / averageTickGap;

        List<Date> previousDateList = new ArrayList<Date>();

        int previousInterval = intervals[0];

        // Starting with the greatest interval, add one of each calendar unit.
        for (int interval : intervals) {
            // Reset the calendar.
            calendar.setTime(new Date(currentLowerBound.get()));
            // Clear the list.
            dateList.clear();
            previousDateList.clear();
            actualInterval = interval;
            if (upperBound.get() != null) {
                // Loop as long we exceeded the upper bound.
                while (calendar.getTime().getTime() < currentUpperBound.get()) {
                    dateList.add(calendar.getTime());
                    calendar.add(interval, 1);
                }
                // Then check the size of the list. If it is greater than the amount of ticks, take that list.
                if (dateList.size() > averageTicks) {
                    calendar.setTime(new Date(currentLowerBound.get()));
                    // Recheck if the previous interval is better suited.
                    while (calendar.getTime().getTime() < currentUpperBound.get()) {
                        previousDateList.add(calendar.getTime());
                        calendar.add(previousInterval, 1);
                    }
                    break;
                }
            }
            previousInterval = interval;
        }
        if (previousDateList.size() - averageTicks > averageTicks - dateList.size()) {
            dateList = previousDateList;
            actualInterval = previousInterval;
        }

        return dateList;
    }

    private int actualInterval = 0;

    @Override
    protected String getTickMarkLabel(Date date) {
        StringConverter<Date> converter = getTickLabelFormatter();
        if (converter != null) {
            return converter.toString(date);
        }
        DateFormat dateFormat;
        switch (actualInterval) {
            case Calendar.YEAR:
                dateFormat = new SimpleDateFormat("yyyy");
                break;
            case Calendar.MONTH:
                dateFormat = new SimpleDateFormat("MM/yyyy");
                break;
            case Calendar.DATE:
            case Calendar.WEEK_OF_YEAR:
            default:
                dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                break;
            case Calendar.HOUR:
            case Calendar.MINUTE:
                dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
                break;
            case Calendar.SECOND:
                dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                break;
            case Calendar.MILLISECOND:
                dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
                break;
        }

        return dateFormat.format(date);
    }

    private ObjectProperty<Date> lowerBound = new ObjectPropertyBase<Date>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "lowerBound";
        }
    };

    public final ObjectProperty<Date> lowerBoundProperty() {
        return lowerBound;
    }

    public final void setLowerBound(Date date) {
        lowerBound.set(date);
    }

    public final Date getLowerBound() {
        return lowerBound.get();
    }


    private ObjectProperty<Date> upperBound = new ObjectPropertyBase<Date>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "upperBound";  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public final ObjectProperty<Date> upperBoundProperty() {
        return upperBound;
    }

    public final void setUpperBound(Date date) {
        upperBound.set(date);
    }

    public final Date getUpperBound() {
        return upperBound.get();
    }

    private final ObjectProperty<StringConverter<Date>> tickLabelFormatter = new ObjectPropertyBase<StringConverter<Date>>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "tickLabelFormatter";
        }
    };

    public final StringConverter<Date> getTickLabelFormatter() {
        return tickLabelFormatter.getValue();
    }

    public final void setTickLabelFormatter(StringConverter<Date> value) {
        tickLabelFormatter.setValue(value);
    }

    public final ObjectProperty<StringConverter<Date>> tickLabelFormatterProperty() {
        return tickLabelFormatter;
    }
}