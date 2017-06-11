// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: xjeffrose/dino/dino.proto at 7:1
package com.xjeffrose.nsfs;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import okio.ByteString;

public final class Dino extends Message<Dino, Dino.Builder> {
  public static final ProtoAdapter<Dino> ADAPTER = new ProtoAdapter_Dino();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_NAME = "";

  public static final String DEFAULT_FAV_COLOR = "";

  /**
   * Name of the Person
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String name;

  /**
   * Fav Color
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String fav_color;

  public Dino(String name, String fav_color) {
    this(name, fav_color, ByteString.EMPTY);
  }

  public Dino(String name, String fav_color, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.name = name;
    this.fav_color = fav_color;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.name = name;
    builder.fav_color = fav_color;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Dino)) return false;
    Dino o = (Dino) other;
    return unknownFields().equals(o.unknownFields())
        && name.equals(o.name)
        && fav_color.equals(o.fav_color);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + name.hashCode();
      result = result * 37 + fav_color.hashCode();
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(", name=").append(name);
    builder.append(", fav_color=").append(fav_color);
    return builder.replace(0, 2, "Dino{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Dino, Builder> {
    public String name;

    public String fav_color;

    public Builder() {
    }

    /**
     * Name of the Person
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Fav Color
     */
    public Builder fav_color(String fav_color) {
      this.fav_color = fav_color;
      return this;
    }

    @Override
    public Dino build() {
      if (name == null
          || fav_color == null) {
        throw Internal.missingRequiredFields(name, "name",
            fav_color, "fav_color");
      }
      return new Dino(name, fav_color, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Dino extends ProtoAdapter<Dino> {
    ProtoAdapter_Dino() {
      super(FieldEncoding.LENGTH_DELIMITED, Dino.class);
    }

    @Override
    public int encodedSize(Dino value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.name)
          + ProtoAdapter.STRING.encodedSizeWithTag(2, value.fav_color)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Dino value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.name);
      ProtoAdapter.STRING.encodeWithTag(writer, 2, value.fav_color);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Dino decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.name(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.fav_color(ProtoAdapter.STRING.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public Dino redact(Dino value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}