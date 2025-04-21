/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import java.util.Objects;

public class Review {
    String reviewText;
    float score;

    public Review() {
    }

    public Review(String reviewText, float score) {
        this.reviewText = reviewText;
        this.score = score;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.reviewText);
        hash = 19 * hash + Float.floatToIntBits(this.score);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Review other = (Review) obj;
        if (Float.floatToIntBits(this.score) != Float.floatToIntBits(other.score)) {
            return false;
        }
        return Objects.equals(this.reviewText, other.reviewText);
    }
    
}
