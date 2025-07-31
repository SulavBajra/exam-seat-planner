export const CreateExamModal = ({ isOpen, onClose, onSubmit }) => {
  const [formData, setFormData] = useState({
    subjectCode: "",
    date: "",
    time: "",
    studentIds: "",
    roomIds: "",
  });

  const [subjects, setSubjects] = useState([]);
  const [rooms, setRooms] = useState([]);

  useEffect(() => {
    if (isOpen) {
      api.get("/api/subjects").then(setSubjects);
      api.get("/api/rooms").then(setRooms);
    }
  }, [isOpen]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const examData = {
      ...formData,
      studentIds: formData.studentIds
        .split(",")
        .map((id) => parseInt(id.trim()))
        .filter((id) => !isNaN(id)),
      roomIds: formData.roomIds
        .split(",")
        .map((id) => parseInt(id.trim()))
        .filter((id) => !isNaN(id)),
    };
    onSubmit(examData);
    setFormData({
      subjectCode: "",
      date: "",
      time: "",
      studentIds: "",
      roomIds: "",
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full flex items-center justify-center z-50">
      <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md mx-4">
        <h3 className="text-lg font-medium text-gray-900 mb-4">
          Create New Exam
        </h3>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Subject
            </label>
            <select
              value={formData.subjectCode}
              onChange={(e) =>
                setFormData({ ...formData, subjectCode: e.target.value })
              }
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select Subject</option>
              {subjects.map((subject) => (
                <option key={subject.subjectCode} value={subject.subjectCode}>
                  {subject.subjectName}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Date
            </label>
            <input
              type="date"
              value={formData.date}
              onChange={(e) =>
                setFormData({ ...formData, date: e.target.value })
              }
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Time
            </label>
            <input
              type="time"
              value={formData.time}
              onChange={(e) =>
                setFormData({ ...formData, time: e.target.value })
              }
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Student IDs (comma-separated)
            </label>
            <input
              type="text"
              value={formData.studentIds}
              onChange={(e) =>
                setFormData({ ...formData, studentIds: e.target.value })
              }
              placeholder="1, 2, 3, 4"
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Room Numbers (comma-separated)
            </label>
            <input
              type="text"
              value={formData.roomIds}
              onChange={(e) =>
                setFormData({ ...formData, roomIds: e.target.value })
              }
              placeholder="101, 102, 201"
              className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              Create Exam
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
