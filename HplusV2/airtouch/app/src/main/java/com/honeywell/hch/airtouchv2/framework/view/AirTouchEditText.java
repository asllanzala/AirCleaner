package com.honeywell.hch.airtouchv2.framework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.honeywell.hch.airtouchv2.R;

/**
 * 
 * 封装系统{android.widget.EditText}功能，并实现能够删除全部输入文字的快捷功能
 *
 * 当有输入内容时，删除全部的按钮会显示；否则不会显示。
 * 
 * Created by lynn.liu on 2014-09-04.
 * 
 */
public class AirTouchEditText extends RelativeLayout {

	private static final int EDIT_TEXT_DEFAULT_STYLE = R.style.text_17_000000;
	private static final int DEFAULT_INPUT_MAX_LENGTH = 300;
//	private static final int EDIT_TEXT_HINT_COLOR = 0x88252525;
	private static final int EDIT_TEXT_HINT_COLOR = 0xffd1d1d1;

	private EditText mEditText; // Value输入框
	private String mHintValue = ""; // hint value
	private int nEditStyle = EDIT_TEXT_DEFAULT_STYLE; // default text style
	private int nEditBackground = 0;
	private int nEditInputType = InputType.TYPE_CLASS_TEXT;
	private TextWatcher mExternTextWatch = null;

	private int nMaxLength = 300;

    private static boolean mIsPasswordEyeOn;
	private ImageView mCleanImg; // 清除图标

    private OnFocusChangeInterface mFocusChangeInterface = null;

    public enum ComponentType {
		REMOVE,
        CLEAN,
        PASSWORD,
    }

    public ImageView getCleanImg() {
        return mCleanImg;
    }

    public static boolean isPasswordEyeOn() {
        return mIsPasswordEyeOn;
    }

    public void setPasswordEyeOn(boolean isPasswordEyeOn) {
        mIsPasswordEyeOn = isPasswordEyeOn;
    }


	public interface OnFocusChangeInterface {
		void onViewChange(View v, boolean hasFocus);
	}

	private OnClickListener mCleanListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mEditText.setText(null);
			showClearButton(false);
		}
	};

    private OnClickListener mPasswordListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mIsPasswordEyeOn) {
                mIsPasswordEyeOn = false;
                mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                mCleanImg.setImageResource(R.drawable.eye_on);
            } else {
                mIsPasswordEyeOn = true;
                mEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mCleanImg.setImageResource(R.drawable.eye_off);
            }
        }
    };

	private OnFocusChangeListener mEditorFocusChangedListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			setSelected(hasFocus);
			showClearButton(hasFocus &&
					!(mEditText.getText().toString().isEmpty()));
			if (null != mFocusChangeInterface) {
				mFocusChangeInterface.onViewChange(AirTouchEditText.this, hasFocus);
			}
		}
	};

	private TextWatcher mTextWatch = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (mExternTextWatch != null) {
				mExternTextWatch.onTextChanged(s, start, before, count);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			if (mExternTextWatch != null) {
				mExternTextWatch.beforeTextChanged(s, start, count, after);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			showClearButton((s.length() > 0));

			if (mExternTextWatch != null) {
				mExternTextWatch.afterTextChanged(s);
			}
		}
	};

    public void setImage(ComponentType type) {
        switch (type) {
			case REMOVE:
				mCleanImg.setImageResource(R.drawable.cancle_search_icon);
				mCleanImg.setOnClickListener(mCleanListener);
				break;

			case CLEAN:
                mCleanImg.setImageResource(R.drawable.delete_et);
                mCleanImg.setOnClickListener(mCleanListener);
                break;

            case PASSWORD:
                mCleanImg.setImageResource(R.drawable.eye_off);
                mCleanImg.setOnClickListener(mPasswordListener);
                break;

            default:

                break;

        }
    }


	public AirTouchEditText(Context context) {
		this(context, null);
	}

	public AirTouchEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFromAttributes(context, attrs);

		// 设置默认背景
		if (nEditBackground > 0) {
//			setBackgroundDrawable(getResources().getDrawable(nEditBackground));
			setBackgroundResource(nEditBackground);
		}

		// 清除按钮
		mCleanImg = new ImageView(context);
		mCleanImg.setId(R.id.clear_button);
		mCleanImg.setImageResource(R.drawable.clear_edit_text);
		mCleanImg.setVisibility(View.INVISIBLE);
		mCleanImg.setOnClickListener(mCleanListener);

		LayoutParams rlp = new LayoutParams(getPixelFromDip(getResources().getDisplayMetrics(), 18), getPixelFromDip(getResources().getDisplayMetrics(), 18));
		rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rlp.topMargin = 7;
		rlp.bottomMargin = 2;
		rlp.leftMargin = 7;
		rlp.rightMargin = 2;
		addView(mCleanImg, rlp);

		// 输入框
		mEditText = new EditText(context);

		// 输入框不设置固定ID，防止同页面多个控件的情况下，onSaveInstanceState方法赋值覆盖
		// mEditText.setId(ID_EDIT_TEXT);
		mEditText.setBackgroundResource(0);
		mEditText.setPadding(getPixelFromDip(getResources().getDisplayMetrics(), 0), 0, 0, 0);
		mEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		mEditText.setTextAppearance(getContext(), nEditStyle);
		mEditText.setHint(mHintValue);
		mEditText.setInputType(nEditInputType);
		setEditorHintColor(EDIT_TEXT_HINT_COLOR);
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(nMaxLength) });
		rlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rlp.addRule(RelativeLayout.LEFT_OF, R.id.clear_button);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);

		addView(mEditText, rlp);

		mEditText.addTextChangedListener(mTextWatch);
		mEditText.setOnFocusChangeListener(mEditorFocusChangedListener);
	}

	private void initFromAttributes(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AirTouchEditText);
			nEditStyle = ta.getResourceId(R.styleable.AirTouchEditText_edit_appearance,
                    EDIT_TEXT_DEFAULT_STYLE);
			nEditBackground = ta.getResourceId(R.styleable.AirTouchEditText_edit_background, 0);
			mHintValue = ta.getString(R.styleable.AirTouchEditText_edit_hint_value);
			nEditInputType = ta.getInt(R.styleable.AirTouchEditText_edit_inputType,
                    InputType.TYPE_CLASS_TEXT);
			nMaxLength = ta.getInt(R.styleable.AirTouchEditText_edit_maxLength, 300);
			ta.recycle();
		} else {
			nEditStyle = EDIT_TEXT_DEFAULT_STYLE;
			mHintValue = null;
			nEditInputType = InputType.TYPE_CLASS_TEXT;
			nMaxLength = 300;
		}
	}

	public void setFocusChangeInterface(OnFocusChangeInterface mFocusChangeInterface) {
		this.mFocusChangeInterface = mFocusChangeInterface;
	}

	/**
	 * 
	 * @return 获取当前editor的输入text
	 */
	public String getEditorText() {
		return mEditText.getText().toString();
	}
	

	/**
	 * 设置背景图片资源
	 * 
	 * @param res
	 *            需要显示的背景图片
	 */
	public void setBackground(int res) {
//		setBackgroundDrawable(getResources().getDrawable(res));
		setBackgroundResource(res);
	}

	/**
	 * 设置editor的左右padding值
	 * 
	 * @param left
	 *            左padding值(in dimension format)
	 * @param right
	 *            右padding值(in dimension format)
	 */
	public void setPadding(int left, int right) {
		mEditText.setPadding(getPixelFromDip(getResources().getDisplayMetrics(), left), 0, getPixelFromDip(getResources().getDisplayMetrics(), right), 0);
	}

	/**
	 * 设置editor显示的hint文本
	 * 
	 * @param text
	 *            hint文本
	 */
	public void setEditorHint(CharSequence text) {
		mEditText.setHint(text);
	}

	/**
	 * 设置editor初始显示的文本
	 * 
	 * @param text
	 *            初始显示的文本
	 */
	public void setEditorText(CharSequence text) {
		mEditText.setText(text);
		if (null != text) {
			showClearButton(mEditText.hasFocus() && !(text.toString().isEmpty()));
		}
	}

	/**
	 * 设置editor的输入类型
	 * 
	 * @param inputType
	 *            输入类型
	 */
	public void setInputType(int inputType) {
		mEditText.setInputType(inputType);
	}

	/**
	 * 设置editor的最大输入长度
	 * 
	 * @param maxLength
	 *            最大长度
	 */
	public void setInputMaxLength(int maxLength) {
		if (maxLength >= 0 && maxLength <= DEFAULT_INPUT_MAX_LENGTH) {
			mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength) });
		}
	}

	/**
	 * 添加editor的文本改变监听Listener
	 * 
	 * @param textWatcher
	 *            文本改变的监听对象
	 */
	public void setEditorWatchListener(TextWatcher textWatcher) {
		mExternTextWatch = textWatcher;
	}

	/**
	 * 
	 * @return 获取控件内部实际的EditText控件
	 */
	public EditText getEditText() {
		return mEditText;
	}

	/**
	 * 移除editor的文本改变监听事件
	 * 
	 * @param textWatcher
	 *            外边改变的监听对象
	 */
	public void removeEditorWatchListener(TextWatcher textWatcher) {
		if (textWatcher != null) {
			mEditText.removeTextChangedListener(textWatcher);
		}
	}

	/**
	 * 设置editor的hint文本
	 * 
	 * @param text
	 *            hint文本
	 */
	public void setEditorHint(String text) {
		mEditText.setHint(text);
	}

	/**
	 * 设置editor的hint颜色
	 * 
	 * @param color
	 *            hint颜色值
	 */
	public void setEditorHintColor(int color) {
		mEditText.setHintTextColor(color);
	}

	/**
	 * 设置editor的文本输入过滤器
	 * 
	 * @param filters
	 *            过滤器列表
	 */
	public void setEditorFilters(InputFilter[] filters) {
		if (filters != null) {
			mEditText.setFilters(filters);
		}
	}

	/**
	 * 设置editor显示文本的式样
	 * 
	 * @param style
	 *            文本式样
	 */
	public void setEditTextStyle(int style) {
		mEditText.setTextAppearance(getContext(), style);
	}

	/**
	 * 设置是否显示清除按钮
	 * 
	 * @param isShow
	 *            true显示, false不显示
	 */
	public void showClearButton(boolean isShow) {
		mCleanImg.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
	}
	
	/**
	* Dip转换为实际屏幕的像素值
	* 
	* @param dm
	*            设备显示对象描述
	* @param dip
	*            dip值
	* @return 匹配当前屏幕的像素值
	*/
	public static int getPixelFromDip(DisplayMetrics dm, float dip) {
		return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm) + 0.5f);
	}

}
