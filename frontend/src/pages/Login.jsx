import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("wallet@test.com");
  const [password, setPassword] = useState("123456");
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    try {
      const res = await api.post("/auth/login", { email, password });

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("role", res.data.role);
      localStorage.setItem("email", res.data.email);

      navigate("/");
      window.location.reload();
    } catch (err) {
      setError("Invalid email or password");
    }
  }

  return (
    <div className="max-w-md mx-auto bg-white p-8 rounded-2xl shadow-sm">
      <h2 className="text-3xl font-bold text-slate-900 mb-6">Login</h2>

      {error && <p className="mb-4 text-red-600">{error}</p>}

      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          className="w-full border rounded-xl px-4 py-3"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
        />

        <input
          className="w-full border rounded-xl px-4 py-3"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
        />

        <button className="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold">
          Login
        </button>
      </form>
    </div>
  );
}

export default Login;