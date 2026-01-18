interface AuthInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export const AuthInput = ({ label, error, className = "", ...props }: AuthInputProps) => {
  return (
    <div className="w-full">
      <label className="block text-sm font-medium text-slate-300 mb-1">
        {label}
      </label>
      <input
        className={`w-full px-4 py-2 bg-slate-800 border rounded-lg focus:ring-2 focus:outline-none text-white transition-all
          ${error 
            ? 'border-red-500 focus:ring-red-500' 
            : 'border-slate-600 focus:ring-emerald-500 hover:border-slate-500'
          } ${className}`}
        {...props}
      />
    </div>
  );
};