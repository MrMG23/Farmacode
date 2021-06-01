// Generated by view binder compiler. Do not edit!
package com.bangkit.farmacode.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.bangkit.farmacode.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentOnBoarding3Binding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout frameLayout3;

  @NonNull
  public final TextView ob3Desc;

  @NonNull
  public final Button ob3Finish;

  @NonNull
  public final ImageView ob3Logo;

  @NonNull
  public final TextView ob3Title;

  private FragmentOnBoarding3Binding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout frameLayout3, @NonNull TextView ob3Desc, @NonNull Button ob3Finish,
      @NonNull ImageView ob3Logo, @NonNull TextView ob3Title) {
    this.rootView = rootView;
    this.frameLayout3 = frameLayout3;
    this.ob3Desc = ob3Desc;
    this.ob3Finish = ob3Finish;
    this.ob3Logo = ob3Logo;
    this.ob3Title = ob3Title;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentOnBoarding3Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentOnBoarding3Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_on_boarding3, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentOnBoarding3Binding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout frameLayout3 = (ConstraintLayout) rootView;

      id = R.id.ob3_desc;
      TextView ob3Desc = rootView.findViewById(id);
      if (ob3Desc == null) {
        break missingId;
      }

      id = R.id.ob3_finish;
      Button ob3Finish = rootView.findViewById(id);
      if (ob3Finish == null) {
        break missingId;
      }

      id = R.id.ob3_logo;
      ImageView ob3Logo = rootView.findViewById(id);
      if (ob3Logo == null) {
        break missingId;
      }

      id = R.id.ob3_title;
      TextView ob3Title = rootView.findViewById(id);
      if (ob3Title == null) {
        break missingId;
      }

      return new FragmentOnBoarding3Binding((ConstraintLayout) rootView, frameLayout3, ob3Desc,
          ob3Finish, ob3Logo, ob3Title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
