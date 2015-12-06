/**
 * Copyright (C) 2015 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.vanilla.mapper;

import java.util.Date;

public class BazObject {

    private String label;
    private int age;
    private Date startDate;
    private String birthday;
    private Float pct;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Float getPct() {
        return pct;
    }

    public void setPct(Float pct) {
        this.pct = pct;
    }

    @Override
    public String toString() {
        return "BazObject{" +
            "label='" + label + '\'' +
            ", age=" + age +
            ", startDate=" + startDate +
            ", birthday='" + birthday + '\'' +
            ", pct=" + pct +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BazObject bazObject = (BazObject) o;

        if (age != bazObject.age) return false;
        if (label != null ? !label.equals(bazObject.label) : bazObject.label != null) return false;
        if (startDate != null ? !startDate.equals(bazObject.startDate) : bazObject.startDate != null) return false;
        if (birthday != null ? !birthday.equals(bazObject.birthday) : bazObject.birthday != null) return false;
        return !(pct != null ? !pct.equals(bazObject.pct) : bazObject.pct != null);

    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (pct != null ? pct.hashCode() : 0);
        return result;
    }
}
