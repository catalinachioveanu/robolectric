package org.robolectric.shadows;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.TestRunners;
import org.robolectric.fakes.RoboAttributeSet;
import org.robolectric.res.Attribute;
import org.robolectric.res.ResName;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(TestRunners.MultiApiWithDefaults.class)
public class ShadowEditTextTest {
  private EditText editText;

  @Before
  public void setup() {
    AttributeSet attributeSet = RoboAttributeSet.create(RuntimeEnvironment.application,
        new Attribute("android:attr/maxLength", "5", R.class.getPackage().getName())
    );

    editText = new EditText(application, attributeSet);
  }

  @Test
  public void shouldRespectMaxLength() throws Exception {
    editText.setText("0123456678");
    assertThat(editText.getText().toString()).isEqualTo("01234");
  }

  @Test
  public void shouldAcceptNullStrings() {
    editText.setText(null);
    assertThat(editText.getText().toString()).isEqualTo("");
  }

  @Test
  public void givenInitializingWithAttributeSet_whenMaxLengthDefined_thenRestrictTextLengthToMaxLength() {
    int maxLength = anyInteger();
    AttributeSet attrs = attributeSetWithMaxLength(maxLength);
    EditText editText = new EditText(RuntimeEnvironment.application, attrs);
    String excessiveInput = stringOfLength(maxLength * 2);

    editText.setText(excessiveInput);

    assertThat((CharSequence) editText.getText().toString()).isEqualTo(excessiveInput.subSequence(0, maxLength));
  }

  @Test
  public void givenInitializingWithAttributeSet_whenMaxLengthNotDefined_thenTextLengthShouldHaveNoRestrictions() {
    AttributeSet attrs = attributeSetWithoutMaxLength();
    EditText editText = new EditText(RuntimeEnvironment.application, attrs);
    String input = anyString();

    editText.setText(input);

    assertThat(editText.getText().toString()).isEqualTo(input);
  }

  @Test
  public void whenInitializingWithoutAttributeSet_thenTextLengthShouldHaveNoRestrictions() {
    EditText editText = new EditText(RuntimeEnvironment.application);
    String input = anyString();

    editText.setText(input);

    assertThat(editText.getText().toString()).isEqualTo(input);
  }

  @Test
  public void testSelectAll() {
    EditText editText = new EditText(RuntimeEnvironment.application);
    editText.setText("foo");

    editText.selectAll();

    assertThat(editText.getSelectionStart()).isEqualTo(0);
    assertThat(editText.getSelectionEnd()).isEqualTo(3);
  }

  @Test
  public void shouldGetHintFromXml() {
    Context context = RuntimeEnvironment.application;
    LayoutInflater inflater = LayoutInflater.from(context);
    EditText editText = (EditText) inflater.inflate(R.layout.edit_text, null);
    assertThat(editText.getHint().toString()).isEqualTo("Hello, Hint");
  }

  private String anyString() {
    return stringOfLength(anyInteger());
  }

  private String stringOfLength(int length) {
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < length; i++)
      stringBuilder.append('x');

    return stringBuilder.toString();
  }

  private int anyInteger() {
    return new Random().nextInt(1000) + 1;
  }

  private AttributeSet attributeSetWithMaxLength(int maxLength) {
    return RoboAttributeSet.create(RuntimeEnvironment.application,
        new Attribute(new ResName("android", "attr", "maxLength"), maxLength + "", "android")
    );
  }

  private AttributeSet attributeSetWithoutMaxLength() {
    return RoboAttributeSet.create(RuntimeEnvironment.application);
  }
}
