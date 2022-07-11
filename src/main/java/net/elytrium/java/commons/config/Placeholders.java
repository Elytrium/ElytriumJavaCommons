/*
 * Copyright (C) 2022 Elytrium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.elytrium.java.commons.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Placeholders {

  static class Data {
    String[] placeholders;
    String value;

    @Override
    public int hashCode() {
      return 31 * Arrays.hashCode(this.placeholders) + this.value.hashCode();
    }

  }

  static final Map<String, Data> data = new HashMap<>();

  static boolean isID(String id) {
    byte[] bytes = id.getBytes(StandardCharsets.UTF_8);
    return bytes[0] == 0x15 && bytes[1] == 0x7F && id.substring(2).matches("^-?\\d+$");
  }

  static Data dataFromID(String id) {
    if (!isID(id)) {
      System.out.println(id);
      throw new IllegalStateException("Invalid field ID. Reload config.");
    }
    return Placeholders.data.get(id);
  }

  public static String replace(String id, Object... values) {
    Data data = dataFromID(id);
    if (values.length < data.placeholders.length) {
      throw new IllegalStateException("Too few values");
    }
    String stringValue = data.value;
    for (int i = 0; i < data.placeholders.length; i++) {
      stringValue = stringValue.replace(toPlaceholderName(data.placeholders[i]), String.valueOf(values[i]));
    }
    return stringValue;
  }

  private static String toPlaceholderName(String name) {
    if (name.matches("^\\{[A-Z\\d_]+(?<!_)}$")) {
      return name;
    } else if (name.matches("^[a-z\\d-]+(?<!-)$")) {
      return '{' + name.toUpperCase(Locale.ROOT).replace('-', '_') + '}';
    } else {
      throw new IllegalStateException("Invalid placeholder: " + name);
    }
  }

}
