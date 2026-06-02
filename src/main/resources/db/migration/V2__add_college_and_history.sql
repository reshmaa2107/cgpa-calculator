ALTER TABLE student ADD COLUMN IF NOT EXISTS college VARCHAR(255);
ALTER TABLE student ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE student ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'STUDENT';

UPDATE student SET role = 'STUDENT' WHERE role IS NULL;
ALTER TABLE student ALTER COLUMN role SET NOT NULL;

CREATE TABLE IF NOT EXISTS marks_history (
                                             id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id   UUID NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    semester_no  INTEGER NOT NULL,
    course_name  VARCHAR(255) NOT NULL,
    course_code  VARCHAR(50),
    credit_hours INTEGER NOT NULL,
    letter_grade VARCHAR(5) NOT NULL,
    grade_points NUMERIC(4,2) NOT NULL,
    recorded_at  TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX idx_history_student ON marks_history(student_id);