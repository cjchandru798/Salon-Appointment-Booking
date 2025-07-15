export default function TailwindTest() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-green-400 to-blue-500">
      <div className="bg-white p-6 rounded shadow text-center">
        <h1 className="text-2xl font-bold text-indigo-600">✅ Tailwind is working!</h1>
        <p className="mt-2 text-gray-600">If you see this styled, you're good to go.</p>
      </div>
    </div>
  );
}
