import { useLoginForm } from "../hooks/useLoginForm";

const LoginForm = () => {
  const { values, error, isLoading, handleChange, handleSubmit } = useLoginForm();

  return (
    <form onSubmit={handleSubmit} className="space-y-4 mt-8">
      <div>
        <label className="block text-sm font-medium text-slate-300">
          Email
        </label>
        <input
          type="email"
          name="email"
          required
          className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white disabled:opacity-50"
          value={values.email}
          onChange={handleChange}
          disabled={isLoading}
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-slate-300">
          Password
        </label>
        <input
          type="password"
          name="password"
          required
          className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white disabled:opacity-50"
          value={values.password}
          onChange={handleChange}
          disabled={isLoading}
        />
      </div>

      {error && <p className="text-red-500 text-sm text-center">{error}</p>}

      <button
        type="submit"
        disabled={isLoading}
        className="w-full py-3 font-bold text-white bg-emerald-600 rounded-lg hover:bg-emerald-500 transition-all shadow-lg shadow-emerald-500/20 disabled:bg-slate-600 disabled:cursor-not-allowed"
      >
        {isLoading ? "로그인 중..." : "로그인"}
      </button>
    </form>
  );
};

export default LoginForm;