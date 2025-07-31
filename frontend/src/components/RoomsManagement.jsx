const RoomsManagement = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadRooms();
  }, []);

  const loadRooms = async () => {
    setLoading(true);
    try {
      const data = await api.get("/api/rooms");
      setRooms(data);
    } catch (error) {
      console.error("Error loading rooms:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-900">Room Management</h2>
        <button className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 transition-colors">
          <Plus className="w-4 h-4 mr-2" />
          Add Room
        </button>
      </div>

      {loading ? (
        <div className="text-center py-8">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {rooms.map((room) => (
            <div
              key={room.roomNo}
              className="bg-white overflow-hidden shadow rounded-lg"
            >
              <div className="p-5">
                <div className="flex items-center justify-between">
                  <h3 className="text-lg font-medium text-gray-900">
                    Room {room.roomNo}
                  </h3>
                  <MapPin className="h-5 w-5 text-gray-400" />
                </div>
                <div className="mt-4 space-y-2">
                  <p className="text-sm text-gray-500">
                    Capacity:{" "}
                    <span className="font-medium text-gray-900">
                      {room.seatingCapacity}
                    </span>
                  </p>
                  <p className="text-sm text-gray-500">
                    Layout:{" "}
                    <span className="font-medium text-gray-900">
                      {room.numRow} x {room.numColumn}
                    </span>
                  </p>
                </div>
                <div className="mt-4 flex space-x-2">
                  <button className="flex-1 bg-blue-50 text-blue-700 px-3 py-2 rounded text-sm hover:bg-blue-100 transition-colors">
                    View Details
                  </button>
                  <button className="flex-1 bg-gray-50 text-gray-700 px-3 py-2 rounded text-sm hover:bg-gray-100 transition-colors">
                    Edit
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
