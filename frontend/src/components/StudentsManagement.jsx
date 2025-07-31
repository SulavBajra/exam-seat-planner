const StudentsManagement = () => (
  <div className="text-center py-12">
    <Users className="mx-auto h-12 w-12 text-gray-400" />
    <h3 className="mt-2 text-sm font-medium text-gray-900">
      Student Management
    </h3>
    <p className="mt-1 text-sm text-gray-500">
      Manage student records and registrations
    </p>
    <button className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700">
      <Plus className="w-4 h-4 mr-2" />
      Add Student
    </button>
  </div>
);

const Reports = () => (
  <div className="text-center py-12">
    <Download className="mx-auto h-12 w-12 text-gray-400" />
    <h3 className="mt-2 text-sm font-medium text-gray-900">
      Reports & Analytics
    </h3>
    <p className="mt-1 text-sm text-gray-500">
      Generate seating charts and analysis reports
    </p>
    <button className="mt-4 inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700">
      <Download className="w-4 h-4 mr-2" />
      Generate Report
    </button>
  </div>
);
