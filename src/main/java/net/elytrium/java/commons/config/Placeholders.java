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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class Placeholders {

  private static final Pattern EXACTLY_MATCHES = Pattern.compile("^\\{(?!_)[A-Z\\d_]+(?<!_)}$");
  private static final Pattern LOWERCASE = Pattern.compile("^(?!-)[a-z\\d-]+(?<!-)$");
  private static final Pattern UPPERCASE = Pattern.compile("^(?!_)[A-Z\\d_]+(?<!_)$");

  static class Data {
    String[] placeholders;
    String value;

    @Override
    public int hashCode() {
      return 31 * Arrays.hashCode(this.placeholders) + this.value.hashCode();
    }
  }

  static final Map<String, Data> data = new HashMap<>();

  static Data dataFromID(String id) {
    if (!data.containsKey(id)) {
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
    if (EXACTLY_MATCHES.matcher(name).matches()) {
      return name;
    } else if (LOWERCASE.matcher(name).matches()) {
      return '{' + name.toUpperCase(Locale.ROOT).replace('-', '_') + '}';
    } else if (UPPERCASE.matcher(name).matches()) {
      return '{' + name + '}';
    } else {
      throw new IllegalStateException("Invalid placeholder: " + name);
    }
  }

}
