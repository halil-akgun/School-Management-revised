# SCHOOL MANAGEMENT API (revised)
- The checkMeetConflict() method has been revised to control teachers as well.
- The meetSave() and the meetUpdate() methods have been updated to inform which students cannot attend the meet.
- In all getAllWithPage() methods, String data types used for sorting direction have been converted to Sort.Direction data types.
- In the updateLessonProgram() method, the updated lesson program and the other lesson programs registered in the database were compared, and it was checked whether there was a conflict between the students and the teachers.
- All methods that provide POJO-DTO transformations have been gathered in a single package called mapper.
- Added validation to prevent an invalid credit score entry.