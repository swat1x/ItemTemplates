package ru.swat1x.itemtemplates.management;


import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreationResult {

  boolean success;
  ItemTemplate template;

  public static CreationResult success(ItemTemplate template) {
    return new CreationResult(true, template);
  }

  public static CreationResult failed() {
    return new CreationResult(false, null);
  }

}
